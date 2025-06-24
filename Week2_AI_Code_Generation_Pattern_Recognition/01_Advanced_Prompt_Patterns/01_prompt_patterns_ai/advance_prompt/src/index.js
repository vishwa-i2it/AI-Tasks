const express = require('express');
const { body, validationResult } = require('express-validator');
const {
    authenticateUser,
    registerUser,
    verifyToken,
    getAllUsers
} = require('./auth');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// CORS middleware for development
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    if (req.method === 'OPTIONS') {
        res.sendStatus(200);
    } else {
        next();
    }
});

/**
 * Authentication middleware to verify JWT tokens
 * @param {Object} req - Express request object
 * @param {Object} res - Express response object
 * @param {Function} next - Express next function
 */
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

    if (!token) {
        return res.status(401).json({
            success: false,
            error: 'Access token required'
        });
    }

    const decoded = verifyToken(token);
    if (!decoded) {
        return res.status(403).json({
            success: false,
            error: 'Invalid or expired token'
        });
    }

    req.user = decoded;
    next();
}

// Validation middleware
const validateRegistration = [
    body('email')
        .isEmail()
        .normalizeEmail()
        .withMessage('Please provide a valid email address'),
    body('password')
        .isLength({ min: 8 })
        .withMessage('Password must be at least 8 characters long')
        .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/)
        .withMessage('Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)')
];

const validateLogin = [
    body('email')
        .isEmail()
        .normalizeEmail()
        .withMessage('Please provide a valid email address'),
    body('password')
        .notEmpty()
        .withMessage('Password is required')
];

/**
 * @route   POST /api/auth/register
 * @desc    Register a new user
 * @access  Public
 */
app.post('/api/auth/register', validateRegistration, async (req, res) => {
    try {
        // Check for validation errors
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({
                success: false,
                error: 'Validation failed',
                details: errors.array()
            });
        }

        const { email, password } = req.body;

        // Register user using our authentication system
        const result = await registerUser(email, password);

        if (result.success) {
            res.status(201).json({
                success: true,
                message: 'User registered successfully',
                token: result.token,
                user: result.user
            });
        } else {
            res.status(400).json({
                success: false,
                error: result.error
            });
        }
    } catch (error) {
        console.error('Registration error:', error);
        res.status(500).json({
            success: false,
            error: 'Internal server error during registration'
        });
    }
});

/**
 * @route   POST /api/auth/login
 * @desc    Authenticate user and return JWT token
 * @access  Public
 */
app.post('/api/auth/login', validateLogin, async (req, res) => {
    try {
        // Check for validation errors
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({
                success: false,
                error: 'Validation failed',
                details: errors.array()
            });
        }

        const { email, password } = req.body;

        // Authenticate user using our authentication system
        const result = await authenticateUser(email, password);

        if (result.success) {
            res.json({
                success: true,
                message: 'Authentication successful',
                token: result.token,
                user: result.user
            });
        } else {
            res.status(401).json({
                success: false,
                error: result.error
            });
        }
    } catch (error) {
        console.error('Authentication error:', error);
        res.status(500).json({
            success: false,
            error: 'Internal server error during authentication'
        });
    }
});

/**
 * @route   GET /api/auth/me
 * @desc    Get current user information
 * @access  Private
 */
app.get('/api/auth/me', authenticateToken, (req, res) => {
    res.json({
        success: true,
        user: {
            userId: req.user.userId,
            email: req.user.email,
            iat: req.user.iat,
            exp: req.user.exp
        }
    });
});

/**
 * @route   GET /api/auth/users
 * @desc    Get all users (for demonstration purposes)
 * @access  Private
 */
app.get('/api/auth/users', authenticateToken, (req, res) => {
    const users = getAllUsers();
    res.json({
        success: true,
        users: users,
        count: users.length
    });
});

/**
 * @route   GET /api/health
 * @desc    Health check endpoint
 * @access  Public
 */
app.get('/api/health', (req, res) => {
    res.json({
        success: true,
        message: 'Authentication service is running',
        timestamp: new Date().toISOString(),
        version: '1.0.0'
    });
});

/**
 * @route   GET /
 * @desc    API documentation and usage examples
 * @access  Public
 */
app.get('/', (req, res) => {
    res.json({
        message: 'Secure Authentication API',
        version: '1.0.0',
        endpoints: {
            'POST /api/auth/register': {
                description: 'Register a new user',
                body: {
                    email: 'string (valid email format)',
                    password: 'string (min 8 chars, uppercase, lowercase, number, special char)'
                },
                response: {
                    success: 'boolean',
                    token: 'string (JWT)',
                    user: 'object'
                }
            },
            'POST /api/auth/login': {
                description: 'Authenticate user and get JWT token',
                body: {
                    email: 'string',
                    password: 'string'
                },
                response: {
                    success: 'boolean',
                    token: 'string (JWT)',
                    user: 'object'
                }
            },
            'GET /api/auth/me': {
                description: 'Get current user information',
                headers: {
                    'Authorization': 'Bearer <JWT_TOKEN>'
                },
                response: {
                    success: 'boolean',
                    user: 'object'
                }
            },
            'GET /api/auth/users': {
                description: 'Get all users (demo only)',
                headers: {
                    'Authorization': 'Bearer <JWT_TOKEN>'
                },
                response: {
                    success: 'boolean',
                    users: 'array',
                    count: 'number'
                }
            }
        },
        features: [
            'Email validation using RFC 5322 compliant regex',
            'Password strength validation with regex',
            'Bcrypt password hashing with 12 salt rounds',
            'JWT token generation and verification',
            'Comprehensive error handling',
            'Async/await best practices',
            'Express.js integration with validation'
        ]
    });
});

// Error handling middleware
app.use((err, req, res, next) => {
    console.error('Unhandled error:', err);
    res.status(500).json({
        success: false,
        error: 'Internal server error'
    });
});

// 404 handler
app.use('*', (req, res) => {
    res.status(404).json({
        success: false,
        error: 'Endpoint not found'
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`🚀 Authentication server running on port ${PORT}`);
    console.log(`📖 API Documentation: http://localhost:${PORT}`);
    console.log(`🔗 Health Check: http://localhost:${PORT}/api/health`);
    console.log('\n📝 Usage Examples:');
    console.log('1. Register: POST /api/auth/register');
    console.log('2. Login: POST /api/auth/login');
    console.log('3. Get Profile: GET /api/auth/me (with Authorization header)');
    console.log('4. Get Users: GET /api/auth/users (with Authorization header)');
});

module.exports = app; 