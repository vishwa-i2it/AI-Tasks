# Secure User Authentication System

A comprehensive, production-ready authentication system built with Node.js that implements secure user authentication with JWT tokens, bcrypt password hashing, and robust input validation.

## 🚀 Features

- **Email Validation**: RFC 5322 compliant regex validation
- **Password Strength**: Enforces strong password requirements with regex
- **Secure Hashing**: Bcrypt password hashing with 12 salt rounds
- **JWT Tokens**: Secure token-based authentication
- **Input Validation**: Comprehensive validation using regex patterns
- **Error Handling**: Proper error handling for all scenarios
- **Async Operations**: Follows JavaScript best practices for async/await
- **Comprehensive Documentation**: Full JSDoc comments and examples
- **Express.js Integration**: Ready-to-use Express.js middleware
- **Testing**: Comprehensive test suite with Jest

## 📋 Requirements

- Node.js 14.0 or higher
- npm or yarn package manager

## 🛠️ Installation

1. **Clone or download the project files**

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Set environment variables (optional):**
   ```bash
   export JWT_SECRET="your-super-secret-jwt-key-change-in-production"
   ```

4. **Run the server:**
   ```bash
   npm start
   ```

## 📖 API Documentation

### Base URL
```
http://localhost:3000
```

### Endpoints

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user_1234567890_abc123",
    "email": "user@example.com",
    "createdAt": "2024-01-01T00:00:00.000Z"
  }
}
```

#### 2. Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Authentication successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user_1234567890_abc123",
    "email": "user@example.com",
    "createdAt": "2024-01-01T00:00:00.000Z"
  }
}
```

#### 3. Get Current User
```http
GET /api/auth/me
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
  "success": true,
  "user": {
    "userId": "user_1234567890_abc123",
    "email": "user@example.com",
    "iat": 1704067200,
    "exp": 1704153600
  }
}
```

#### 4. Get All Users (Demo)
```http
GET /api/auth/users
Authorization: Bearer <JWT_TOKEN>
```

## 🔧 Core Functions

### `authenticateUser(email, password)`

The main authentication function that implements all security requirements:

```javascript
const { authenticateUser } = require('./src/auth');

async function login() {
    const result = await authenticateUser('user@example.com', 'SecurePass123!');
    
    if (result.success) {
        console.log('Token:', result.token);
        console.log('User:', result.user);
    } else {
        console.log('Error:', result.error);
    }
}
```

**Features:**
1. ✅ **Input Validation**: Validates email and password format
2. ✅ **Regex Validation**: Uses RFC 5322 email regex and strong password regex
3. ✅ **Bcrypt Hashing**: Verifies password against bcrypt hash
4. ✅ **JWT Generation**: Returns secure JWT token on success
5. ✅ **Error Handling**: Comprehensive error handling for all scenarios
6. ✅ **Async Operations**: Proper async/await implementation
7. ✅ **JSDoc Documentation**: Complete documentation with examples

### `registerUser(email, password)`

Registers a new user with secure password hashing:

```javascript
const { registerUser } = require('./src/auth');

async function register() {
    const result = await registerUser('newuser@example.com', 'SecurePass123!');
    
    if (result.success) {
        console.log('User registered:', result.user);
        console.log('Token:', result.token);
    } else {
        console.log('Registration failed:', result.error);
    }
}
```

### `verifyToken(token)`

Verifies JWT tokens:

```javascript
const { verifyToken } = require('./src/auth');

function checkToken(token) {
    const decoded = verifyToken(token);
    if (decoded) {
        console.log('Valid token for user:', decoded.email);
    } else {
        console.log('Invalid or expired token');
    }
}
```

## 🔒 Security Features

### Email Validation
- **RFC 5322 Compliant**: Uses industry-standard email regex
- **Case Normalization**: Converts emails to lowercase for consistency
- **Format Checking**: Validates proper email structure

### Password Requirements
- **Minimum Length**: 8 characters
- **Character Types**: Must contain:
  - At least one uppercase letter
  - At least one lowercase letter
  - At least one number
  - At least one special character (@$!%*?&)
- **Regex Pattern**: `/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/`

### Password Hashing
- **Algorithm**: Bcrypt with 12 salt rounds
- **Security**: Industry-standard hashing for password storage
- **Verification**: Secure comparison using bcrypt.compare()

### JWT Security
- **Algorithm**: HS256 (HMAC with SHA-256)
- **Expiration**: 24 hours (configurable)
- **Payload**: Contains user ID, email, and timestamps
- **Verification**: Proper token validation with error handling

## 🧪 Testing

Run the comprehensive test suite:

```bash
npm test
```

**Test Coverage:**
- ✅ Email validation (valid and invalid formats)
- ✅ Password validation (strong and weak passwords)
- ✅ User registration (success and failure cases)
- ✅ User authentication (success and failure cases)
- ✅ JWT token verification
- ✅ Error handling
- ✅ Edge cases and security scenarios

## 📝 Usage Examples

### Basic Authentication Flow

```javascript
const { authenticateUser, registerUser, verifyToken } = require('./src/auth');

async function authenticationExample() {
    try {
        // 1. Register a new user
        const registerResult = await registerUser('john@example.com', 'SecurePass123!');
        
        if (registerResult.success) {
            console.log('✅ User registered successfully');
            console.log('Token:', registerResult.token);
            
            // 2. Authenticate the user
            const authResult = await authenticateUser('john@example.com', 'SecurePass123!');
            
            if (authResult.success) {
                console.log('✅ Authentication successful');
                console.log('User:', authResult.user);
                
                // 3. Verify the token
                const decoded = verifyToken(authResult.token);
                if (decoded) {
                    console.log('✅ Token verified');
                    console.log('User ID:', decoded.userId);
                }
            }
        }
    } catch (error) {
        console.error('❌ Error:', error.message);
    }
}
```

### Express.js Integration

```javascript
const express = require('express');
const { authenticateUser } = require('./src/auth');

const app = express();
app.use(express.json());

app.post('/login', async (req, res) => {
    const { email, password } = req.body;
    
    const result = await authenticateUser(email, password);
    
    if (result.success) {
        res.json({
            success: true,
            token: result.token,
            user: result.user
        });
    } else {
        res.status(401).json({
            success: false,
            error: result.error
        });
    }
});

app.listen(3000, () => {
    console.log('Server running on port 3000');
});
```

## 🔧 Configuration

### Environment Variables

```bash
# JWT Secret Key (change in production!)
JWT_SECRET=your-super-secret-jwt-key-change-in-production

# Server Port
PORT=3000
```

### Security Constants

```javascript
// In src/auth.js
const JWT_SECRET = process.env.JWT_SECRET || 'your-super-secret-jwt-key-change-in-production';
const JWT_EXPIRES_IN = '24h';
const SALT_ROUNDS = 12;
```

## 🚨 Security Considerations

### Production Deployment

1. **Change JWT Secret**: Use a strong, unique secret key
2. **Use HTTPS**: Always use HTTPS in production
3. **Database Storage**: Replace in-memory storage with a database
4. **Rate Limiting**: Implement rate limiting for auth endpoints
5. **CORS Configuration**: Configure CORS properly for your domain
6. **Environment Variables**: Use proper environment variable management

### Additional Security Measures

1. **Password Reset**: Implement secure password reset functionality
2. **Account Lockout**: Add account lockout after failed attempts
3. **Two-Factor Authentication**: Consider adding 2FA
4. **Audit Logging**: Log authentication events
5. **Session Management**: Implement proper session handling

## 📁 Project Structure

```
├── src/
│   ├── auth.js          # Core authentication functions
│   └── index.js         # Express.js server
├── test/
│   └── auth.test.js     # Comprehensive test suite
├── package.json         # Dependencies and scripts
└── README.md           # This documentation
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For questions or issues, please create an issue in the repository or contact the development team.

## 📝 Prompt Examples

### 1. Basic Prompt
```
Create a user authentication function in Node.js.
```

### 2. Chain-of-Thought Prompt
```
I need to build a secure user authentication system. Let me break this down:
Step 1: Accept email and password as input.
Step 2: Validate the input format using regex.
Step 3: Hash the password using bcrypt.
Step 4: Generate a JWT token on successful authentication.
Step 5: Add error handling for invalid credentials.
For each step, please:
- Explain the approach
- Write the code
- Suggest improvements
Start with Step 1.
```

### 3. Context-Rich Prompt
```
Context: I am developing a Node.js backend for a web application that requires secure user authentication. The system should use JWT tokens, bcrypt for password hashing, and robust input validation.

Current Architecture: Node.js with Express, MongoDB for user storage, JWT for authentication.

Technology Stack: Node.js 14+, Express, MongoDB, bcrypt, jsonwebtoken

Constraints: Must follow security best practices, validate all inputs, and provide clear error messages. Async/await should be used for all operations.

Task: Implement a secure user authentication function that validates input, hashes passwords, generates JWT tokens, and handles errors.

Expected Output: Node.js code for registration and login endpoints, with input validation, password hashing, JWT generation, and error handling.

Quality Criteria: Code should be secure, well-documented, tested, and follow best practices for authentication and error handling.
```

---

**Note**: This is a demonstration implementation. For production use, please ensure proper security measures, database integration, and environment-specific configurations are implemented. 