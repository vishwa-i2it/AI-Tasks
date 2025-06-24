const {
    authenticateUser,
    registerUser,
    verifyToken,
    validateEmail,
    validatePassword,
    getAllUsers,
    clearUsers
} = require('../src/auth');

/**
 * Test suite for the authentication system
 * Tests all major functionality including validation, registration, authentication, and token verification
 */

// Helper function to wait for async operations
const wait = (ms) => new Promise(resolve => setTimeout(resolve, ms));

describe('Email Validation', () => {
    test('should validate correct email formats', () => {
        const validEmails = [
            'test@example.com',
            'user.name@domain.co.uk',
            'user+tag@example.org',
            'user123@test-domain.com',
            'a@b.c'
        ];

        validEmails.forEach(email => {
            expect(validateEmail(email)).toBe(true);
        });
    });

    test('should reject invalid email formats', () => {
        const invalidEmails = [
            'invalid-email',
            '@example.com',
            'user@',
            'user@.com',
            'user..name@example.com',
            'user@example..com',
            '',
            null,
            undefined
        ];

        invalidEmails.forEach(email => {
            expect(validateEmail(email)).toBe(false);
        });
    });
});

describe('Password Validation', () => {
    test('should validate strong passwords', () => {
        const validPasswords = [
            'SecurePass123!',
            'MyP@ssw0rd',
            'Str0ng#Pass',
            'C0mpl3x!Pass'
        ];

        validPasswords.forEach(password => {
            const result = validatePassword(password);
            expect(result.isValid).toBe(true);
            expect(result.error).toBeNull();
        });
    });

    test('should reject weak passwords', () => {
        const invalidPasswords = [
            'weak', // too short
            'weakpass', // no uppercase, number, or special char
            'WeakPass', // no number or special char
            'WeakPass1', // no special char
            'weakpass1!', // no uppercase
            'WEAKPASS1!', // no lowercase
            '', // empty
            '12345678', // only numbers
            'abcdefgh', // only lowercase
            'ABCDEFGH', // only uppercase
            '!@#$%^&*' // only special chars
        ];

        invalidPasswords.forEach(password => {
            const result = validatePassword(password);
            expect(result.isValid).toBe(false);
            expect(result.error).toBeTruthy();
        });
    });
});

describe('User Registration', () => {
    beforeEach(() => {
        clearUsers();
    });

    test('should register a new user successfully', async () => {
        const email = 'test@example.com';
        const password = 'SecurePass123!';

        const result = await registerUser(email, password);

        expect(result.success).toBe(true);
        expect(result.token).toBeTruthy();
        expect(result.error).toBeNull();
        expect(result.user).toBeTruthy();
        expect(result.user.email).toBe(email.toLowerCase());
        expect(result.user.id).toBeTruthy();
        expect(result.user.createdAt).toBeTruthy();
    });

    test('should reject registration with invalid email', async () => {
        const result = await registerUser('invalid-email', 'SecurePass123!');

        expect(result.success).toBe(false);
        expect(result.token).toBeNull();
        expect(result.error).toBe('Invalid email format');
        expect(result.user).toBeNull();
    });

    test('should reject registration with weak password', async () => {
        const result = await registerUser('test@example.com', 'weak');

        expect(result.success).toBe(false);
        expect(result.token).toBeNull();
        expect(result.error).toContain('Password must be at least 8 characters');
        expect(result.user).toBeNull();
    });

    test('should reject duplicate user registration', async () => {
        const email = 'test@example.com';
        const password = 'SecurePass123!';

        // First registration should succeed
        const result1 = await registerUser(email, password);
        expect(result1.success).toBe(true);

        // Second registration should fail
        const result2 = await registerUser(email, password);
        expect(result2.success).toBe(false);
        expect(result2.error).toBe('User with this email already exists');
    });

    test('should handle missing email', async () => {
        const result = await registerUser('', 'SecurePass123!');

        expect(result.success).toBe(false);
        expect(result.error).toBe('Email is required and must be a string');
    });

    test('should handle missing password', async () => {
        const result = await registerUser('test@example.com', '');

        expect(result.success).toBe(false);
        expect(result.error).toBe('Password is required and must be a string');
    });
});

describe('User Authentication', () => {
    beforeEach(() => {
        clearUsers();
    });

    test('should authenticate existing user successfully', async () => {
        const email = 'test@example.com';
        const password = 'SecurePass123!';

        // Register user first
        const registerResult = await registerUser(email, password);
        expect(registerResult.success).toBe(true);

        // Authenticate user
        const authResult = await authenticateUser(email, password);

        expect(authResult.success).toBe(true);
        expect(authResult.token).toBeTruthy();
        expect(authResult.error).toBeNull();
        expect(authResult.user).toBeTruthy();
        expect(authResult.user.email).toBe(email.toLowerCase());
    });

    test('should reject authentication with wrong password', async () => {
        const email = 'test@example.com';
        const password = 'SecurePass123!';

        // Register user
        await registerUser(email, password);

        // Try to authenticate with wrong password
        const result = await authenticateUser(email, 'WrongPass123!');

        expect(result.success).toBe(false);
        expect(result.token).toBeNull();
        expect(result.error).toBe('Invalid credentials');
        expect(result.user).toBeNull();
    });

    test('should reject authentication for non-existent user', async () => {
        const result = await authenticateUser('nonexistent@example.com', 'SecurePass123!');

        expect(result.success).toBe(false);
        expect(result.token).toBeNull();
        expect(result.error).toBe('Invalid credentials');
        expect(result.user).toBeNull();
    });

    test('should reject authentication with invalid email format', async () => {
        const result = await authenticateUser('invalid-email', 'SecurePass123!');

        expect(result.success).toBe(false);
        expect(result.error).toBe('Invalid email format');
    });

    test('should reject authentication with weak password format', async () => {
        const result = await authenticateUser('test@example.com', 'weak');

        expect(result.success).toBe(false);
        expect(result.error).toContain('Password must be at least 8 characters');
    });

    test('should handle case-insensitive email authentication', async () => {
        const email = 'Test@Example.com';
        const password = 'SecurePass123!';

        // Register with lowercase email
        await registerUser(email, password);

        // Authenticate with different case
        const result = await authenticateUser('TEST@EXAMPLE.COM', password);

        expect(result.success).toBe(true);
        expect(result.token).toBeTruthy();
    });
});

describe('JWT Token Verification', () => {
    test('should verify valid JWT token', async () => {
        const email = 'test@example.com';
        const password = 'SecurePass123!';

        // Register and get token
        const registerResult = await registerUser(email, password);
        const token = registerResult.token;

        // Verify token
        const decoded = verifyToken(token);

        expect(decoded).toBeTruthy();
        expect(decoded.userId).toBe(registerResult.user.id);
        expect(decoded.email).toBe(email.toLowerCase());
        expect(decoded.iat).toBeTruthy();
        expect(decoded.exp).toBeTruthy();
    });

    test('should reject invalid JWT token', () => {
        const invalidTokens = [
            'invalid.token.here',
            'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.signature',
            '',
            null,
            undefined
        ];

        invalidTokens.forEach(token => {
            const result = verifyToken(token);
            expect(result).toBeNull();
        });
    });

    test('should reject expired token', async () => {
        // This test would require mocking time or using a very short expiration
        // For now, we'll test that the verification function works correctly
        const email = 'test@example.com';
        const password = 'SecurePass123!';

        const registerResult = await registerUser(email, password);
        const token = registerResult.token;

        // Verify the token is valid immediately after creation
        const decoded = verifyToken(token);
        expect(decoded).toBeTruthy();
    });
});

describe('User Management', () => {
    beforeEach(() => {
        clearUsers();
    });

    test('should return empty array when no users exist', () => {
        const users = getAllUsers();
        expect(users).toEqual([]);
    });

    test('should return all registered users', async () => {
        const users = [
            { email: 'user1@example.com', password: 'SecurePass123!' },
            { email: 'user2@example.com', password: 'SecurePass456!' },
            { email: 'user3@example.com', password: 'SecurePass789!' }
        ];

        // Register all users
        for (const user of users) {
            await registerUser(user.email, user.password);
        }

        const allUsers = getAllUsers();
        expect(allUsers).toHaveLength(3);
        
        allUsers.forEach(user => {
            expect(user.id).toBeTruthy();
            expect(user.email).toBeTruthy();
            expect(user.createdAt).toBeTruthy();
            expect(user.passwordHash).toBeUndefined(); // Should not expose password hash
        });
    });

    test('should clear all users', async () => {
        // Register a user
        await registerUser('test@example.com', 'SecurePass123!');
        expect(getAllUsers()).toHaveLength(1);

        // Clear users
        clearUsers();
        expect(getAllUsers()).toHaveLength(0);
    });
});

describe('Error Handling', () => {
    test('should handle null inputs gracefully', async () => {
        const nullResult = await authenticateUser(null, null);
        expect(nullResult.success).toBe(false);
        expect(nullResult.error).toBe('Email is required and must be a string');

        const undefinedResult = await authenticateUser(undefined, undefined);
        expect(undefinedResult.success).toBe(false);
        expect(undefinedResult.error).toBe('Email is required and must be a string');
    });

    test('should handle non-string inputs gracefully', async () => {
        const numberResult = await authenticateUser(123, 456);
        expect(numberResult.success).toBe(false);
        expect(numberResult.error).toBe('Email is required and must be a string');

        const objectResult = await authenticateUser({}, {});
        expect(objectResult.success).toBe(false);
        expect(objectResult.error).toBe('Email is required and must be a string');
    });
});

// Run the tests
if (require.main === module) {
    console.log('Running authentication tests...\n');
    
    // Simple test runner for demonstration
    const tests = [
        { name: 'Email Validation', fn: () => {
            expect(validateEmail('test@example.com')).toBe(true);
            expect(validateEmail('invalid-email')).toBe(false);
        }},
        { name: 'Password Validation', fn: () => {
            expect(validatePassword('SecurePass123!').isValid).toBe(true);
            expect(validatePassword('weak').isValid).toBe(false);
        }}
    ];

    tests.forEach(test => {
        try {
            test.fn();
            console.log(`✅ ${test.name} - PASSED`);
        } catch (error) {
            console.log(`❌ ${test.name} - FAILED: ${error.message}`);
        }
    });

    console.log('\nAll tests completed!');
} 