const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

/**
 * @typedef {Object} AuthResult
 * @property {boolean} success - Whether the authentication was successful
 * @property {string|null} token - JWT token on success, null on failure
 * @property {string|null} error - Error message on failure, null on success
 * @property {Object|null} user - User object on success, null on failure
 */

/**
 * @typedef {Object} User
 * @property {string} id - User ID
 * @property {string} email - User email
 * @property {string} passwordHash - Hashed password
 * @property {Date} createdAt - User creation timestamp
 */

// Configuration constants
const JWT_SECRET = process.env.JWT_SECRET || 'your-super-secret-jwt-key-change-in-production';
const JWT_EXPIRES_IN = '24h';
const SALT_ROUNDS = 12;

/**
 * Validates email format using regex
 * @param {string} email - Email to validate
 * @returns {boolean} True if email format is valid, false otherwise
 */
function validateEmail(email) {
    // RFC 5322 compliant email regex
    const emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
    return emailRegex.test(email);
}

/**
 * Validates password strength using regex
 * @param {string} password - Password to validate
 * @returns {Object} Validation result with isValid boolean and error message
 */
function validatePassword(password) {
    // Password must be at least 8 characters with at least one uppercase, one lowercase, one number, and one special character
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    
    if (!passwordRegex.test(password)) {
        return {
            isValid: false,
            error: 'Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)'
        };
    }
    
    return { isValid: true, error: null };
}

/**
 * In-memory user storage (replace with database in production)
 * @type {Map<string, User>}
 */
const users = new Map();

/**
 * Authenticates a user with email and password
 * @async
 * @param {string} email - User's email address
 * @param {string} password - User's plain text password
 * @returns {Promise<AuthResult>} Authentication result with token or error
 * 
 * @example
 * const result = await authenticateUser('user@example.com', 'SecurePass123!');
 * if (result.success) {
 *   console.log('Token:', result.token);
 * } else {
 *   console.log('Error:', result.error);
 * }
 */
async function authenticateUser(email, password) {
    try {
        // Step 1: Input validation
        if (!email || typeof email !== 'string') {
            return {
                success: false,
                token: null,
                error: 'Email is required and must be a string',
                user: null
            };
        }

        if (!password || typeof password !== 'string') {
            return {
                success: false,
                token: null,
                error: 'Password is required and must be a string',
                user: null
            };
        }

        // Step 2: Email format validation using regex
        if (!validateEmail(email)) {
            return {
                success: false,
                token: null,
                error: 'Invalid email format',
                user: null
            };
        }

        // Step 3: Password strength validation using regex
        const passwordValidation = validatePassword(password);
        if (!passwordValidation.isValid) {
            return {
                success: false,
                token: null,
                error: passwordValidation.error,
                user: null
            };
        }

        // Step 4: Check if user exists
        const user = users.get(email.toLowerCase());
        if (!user) {
            return {
                success: false,
                token: null,
                error: 'Invalid credentials',
                user: null
            };
        }

        // Step 5: Verify password using bcrypt
        const isPasswordValid = await bcrypt.compare(password, user.passwordHash);
        if (!isPasswordValid) {
            return {
                success: false,
                token: null,
                error: 'Invalid credentials',
                user: null
            };
        }

        // Step 6: Generate JWT token
        const tokenPayload = {
            userId: user.id,
            email: user.email,
            iat: Math.floor(Date.now() / 1000)
        };

        const token = jwt.sign(tokenPayload, JWT_SECRET, {
            expiresIn: JWT_EXPIRES_IN,
            algorithm: 'HS256'
        });

        // Step 7: Return success result
        return {
            success: true,
            token: token,
            error: null,
            user: {
                id: user.id,
                email: user.email,
                createdAt: user.createdAt
            }
        };

    } catch (error) {
        // Step 8: Handle unexpected errors
        console.error('Authentication error:', error);
        return {
            success: false,
            token: null,
            error: 'Internal server error during authentication',
            user: null
        };
    }
}

/**
 * Registers a new user with email and password
 * @async
 * @param {string} email - User's email address
 * @param {string} password - User's plain text password
 * @returns {Promise<AuthResult>} Registration result with token or error
 */
async function registerUser(email, password) {
    try {
        // Input validation (same as authenticateUser)
        if (!email || typeof email !== 'string') {
            return {
                success: false,
                token: null,
                error: 'Email is required and must be a string',
                user: null
            };
        }

        if (!password || typeof password !== 'string') {
            return {
                success: false,
                token: null,
                error: 'Password is required and must be a string',
                user: null
            };
        }

        // Email format validation
        if (!validateEmail(email)) {
            return {
                success: false,
                token: null,
                error: 'Invalid email format',
                user: null
            };
        }

        // Password strength validation
        const passwordValidation = validatePassword(password);
        if (!passwordValidation.isValid) {
            return {
                success: false,
                token: null,
                error: passwordValidation.error,
                user: null
            };
        }

        // Check if user already exists
        const normalizedEmail = email.toLowerCase();
        if (users.has(normalizedEmail)) {
            return {
                success: false,
                token: null,
                error: 'User with this email already exists',
                user: null
            };
        }

        // Hash password using bcrypt
        const passwordHash = await bcrypt.hash(password, SALT_ROUNDS);

        // Create new user
        const newUser = {
            id: generateUserId(),
            email: normalizedEmail,
            passwordHash: passwordHash,
            createdAt: new Date()
        };

        // Store user
        users.set(normalizedEmail, newUser);

        // Generate JWT token
        const tokenPayload = {
            userId: newUser.id,
            email: newUser.email,
            iat: Math.floor(Date.now() / 1000)
        };

        const token = jwt.sign(tokenPayload, JWT_SECRET, {
            expiresIn: JWT_EXPIRES_IN,
            algorithm: 'HS256'
        });

        return {
            success: true,
            token: token,
            error: null,
            user: {
                id: newUser.id,
                email: newUser.email,
                createdAt: newUser.createdAt
            }
        };

    } catch (error) {
        console.error('Registration error:', error);
        return {
            success: false,
            token: null,
            error: 'Internal server error during registration',
            user: null
        };
    }
}

/**
 * Verifies a JWT token and returns the decoded payload
 * @param {string} token - JWT token to verify
 * @returns {Object|null} Decoded token payload or null if invalid
 */
function verifyToken(token) {
    try {
        if (!token || typeof token !== 'string') {
            return null;
        }

        const decoded = jwt.verify(token, JWT_SECRET, {
            algorithms: ['HS256']
        });

        return decoded;
    } catch (error) {
        console.error('Token verification error:', error.message);
        return null;
    }
}

/**
 * Generates a unique user ID
 * @returns {string} Unique user ID
 */
function generateUserId() {
    return 'user_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

/**
 * Gets all users (for testing purposes)
 * @returns {User[]} Array of all users
 */
function getAllUsers() {
    return Array.from(users.values()).map(user => ({
        id: user.id,
        email: user.email,
        createdAt: user.createdAt
    }));
}

/**
 * Clears all users (for testing purposes)
 */
function clearUsers() {
    users.clear();
}

module.exports = {
    authenticateUser,
    registerUser,
    verifyToken,
    validateEmail,
    validatePassword,
    getAllUsers,
    clearUsers
}; 