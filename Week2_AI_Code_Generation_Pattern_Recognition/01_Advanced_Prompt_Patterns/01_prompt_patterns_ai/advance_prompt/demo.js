#!/usr/bin/env node

/**
 * Demonstration script for the Secure Authentication System
 * This script shows how to use all the authentication functions step by step
 */

const {
    authenticateUser,
    registerUser,
    verifyToken,
    validateEmail,
    validatePassword,
    getAllUsers,
    clearUsers
} = require('./src/auth');

// Colors for console output
const colors = {
    reset: '\x1b[0m',
    bright: '\x1b[1m',
    red: '\x1b[31m',
    green: '\x1b[32m',
    yellow: '\x1b[33m',
    blue: '\x1b[34m',
    magenta: '\x1b[35m',
    cyan: '\x1b[36m'
};

function log(message, color = 'reset') {
    console.log(`${colors[color]}${message}${colors.reset}`);
}

function logHeader(title) {
    console.log('\n' + '='.repeat(60));
    log(title, 'bright');
    console.log('='.repeat(60));
}

function logStep(step, description) {
    log(`\n${step}. ${description}`, 'cyan');
}

function logSuccess(message) {
    log(`✅ ${message}`, 'green');
}

function logError(message) {
    log(`❌ ${message}`, 'red');
}

function logInfo(message) {
    log(`ℹ️  ${message}`, 'blue');
}

function logWarning(message) {
    log(`⚠️  ${message}`, 'yellow');
}

/**
 * Demonstrates email validation functionality
 */
async function demonstrateEmailValidation() {
    logHeader('Email Validation Demo');
    
    const testEmails = [
        'user@example.com',
        'user.name@domain.co.uk',
        'user+tag@example.org',
        'invalid-email',
        '@example.com',
        'user@',
        'user@.com',
        '',
        null
    ];

    testEmails.forEach((email, index) => {
        const isValid = validateEmail(email);
        const status = isValid ? 'VALID' : 'INVALID';
        const color = isValid ? 'green' : 'red';
        
        log(`${index + 1}. ${email || 'null'} → ${status}`, color);
    });
}

/**
 * Demonstrates password validation functionality
 */
async function demonstratePasswordValidation() {
    logHeader('Password Validation Demo');
    
    const testPasswords = [
        'SecurePass123!',
        'MyP@ssw0rd',
        'weak',
        'weakpass',
        'WeakPass',
        'WeakPass1',
        'weakpass1!',
        'WEAKPASS1!',
        '',
        '12345678',
        'abcdefgh',
        'ABCDEFGH',
        '!@#$%^&*'
    ];

    testPasswords.forEach((password, index) => {
        const result = validatePassword(password);
        const status = result.isValid ? 'VALID' : 'INVALID';
        const color = result.isValid ? 'green' : 'red';
        
        log(`${index + 1}. "${password || 'empty'}" → ${status}`, color);
        if (!result.isValid) {
            log(`   Error: ${result.error}`, 'yellow');
        }
    });
}

/**
 * Demonstrates user registration functionality
 */
async function demonstrateUserRegistration() {
    logHeader('User Registration Demo');
    
    logStep(1, 'Registering a new user with valid credentials');
    const registerResult = await registerUser('demo@example.com', 'SecurePass123!');
    
    if (registerResult.success) {
        logSuccess('User registered successfully!');
        logInfo(`User ID: ${registerResult.user.id}`);
        logInfo(`Email: ${registerResult.user.email}`);
        logInfo(`Created: ${registerResult.user.createdAt}`);
        logInfo(`Token: ${registerResult.token.substring(0, 50)}...`);
        return registerResult.token;
    } else {
        logError(`Registration failed: ${registerResult.error}`);
        return null;
    }
}

/**
 * Demonstrates user authentication functionality
 */
async function demonstrateUserAuthentication(token) {
    logHeader('User Authentication Demo');
    
    logStep(1, 'Authenticating with correct credentials');
    const authResult = await authenticateUser('demo@example.com', 'SecurePass123!');
    
    if (authResult.success) {
        logSuccess('Authentication successful!');
        logInfo(`User: ${authResult.user.email}`);
        logInfo(`Token: ${authResult.token.substring(0, 50)}...`);
    } else {
        logError(`Authentication failed: ${authResult.error}`);
    }
    
    logStep(2, 'Attempting authentication with wrong password');
    const wrongAuthResult = await authenticateUser('demo@example.com', 'WrongPass123!');
    
    if (!wrongAuthResult.success) {
        logWarning(`Expected failure: ${wrongAuthResult.error}`);
    }
    
    logStep(3, 'Attempting authentication with non-existent user');
    const nonExistentResult = await authenticateUser('nonexistent@example.com', 'SecurePass123!');
    
    if (!nonExistentResult.success) {
        logWarning(`Expected failure: ${nonExistentResult.error}`);
    }
}

/**
 * Demonstrates JWT token verification functionality
 */
async function demonstrateTokenVerification(token) {
    logHeader('JWT Token Verification Demo');
    
    if (!token) {
        logError('No token available for verification');
        return;
    }
    
    logStep(1, 'Verifying valid JWT token');
    const decoded = verifyToken(token);
    
    if (decoded) {
        logSuccess('Token verification successful!');
        logInfo(`User ID: ${decoded.userId}`);
        logInfo(`Email: ${decoded.email}`);
        logInfo(`Issued at: ${new Date(decoded.iat * 1000).toISOString()}`);
        logInfo(`Expires at: ${new Date(decoded.exp * 1000).toISOString()}`);
    } else {
        logError('Token verification failed');
    }
    
    logStep(2, 'Testing invalid tokens');
    const invalidTokens = [
        'invalid.token.here',
        'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.signature',
        '',
        null
    ];
    
    invalidTokens.forEach((invalidToken, index) => {
        const result = verifyToken(invalidToken);
        if (!result) {
            logWarning(`Invalid token ${index + 1} correctly rejected`);
        } else {
            logError(`Invalid token ${index + 1} incorrectly accepted`);
        }
    });
}

/**
 * Demonstrates user management functionality
 */
async function demonstrateUserManagement() {
    logHeader('User Management Demo');
    
    logStep(1, 'Registering multiple users');
    const users = [
        { email: 'user1@example.com', password: 'SecurePass123!' },
        { email: 'user2@example.com', password: 'SecurePass456!' },
        { email: 'user3@example.com', password: 'SecurePass789!' }
    ];
    
    for (const user of users) {
        const result = await registerUser(user.email, user.password);
        if (result.success) {
            logSuccess(`Registered: ${user.email}`);
        } else {
            logError(`Failed to register ${user.email}: ${result.error}`);
        }
    }
    
    logStep(2, 'Retrieving all users');
    const allUsers = getAllUsers();
    logInfo(`Total users: ${allUsers.length}`);
    
    allUsers.forEach((user, index) => {
        logInfo(`User ${index + 1}: ${user.email} (ID: ${user.id})`);
    });
    
    logStep(3, 'Clearing all users');
    clearUsers();
    const remainingUsers = getAllUsers();
    logInfo(`Users after clearing: ${remainingUsers.length}`);
}

/**
 * Demonstrates error handling functionality
 */
async function demonstrateErrorHandling() {
    logHeader('Error Handling Demo');
    
    logStep(1, 'Testing null inputs');
    const nullResult = await authenticateUser(null, null);
    logWarning(`Null inputs result: ${nullResult.error}`);
    
    logStep(2, 'Testing undefined inputs');
    const undefinedResult = await authenticateUser(undefined, undefined);
    logWarning(`Undefined inputs result: ${undefinedResult.error}`);
    
    logStep(3, 'Testing non-string inputs');
    const numberResult = await authenticateUser(123, 456);
    logWarning(`Number inputs result: ${numberResult.error}`);
    
    logStep(4, 'Testing empty strings');
    const emptyResult = await authenticateUser('', '');
    logWarning(`Empty string inputs result: ${emptyResult.error}`);
}

/**
 * Demonstrates the complete authentication flow
 */
async function demonstrateCompleteFlow() {
    logHeader('Complete Authentication Flow Demo');
    
    logStep(1, 'Starting fresh - clearing all users');
    clearUsers();
    
    logStep(2, 'Registering a new user');
    const registerResult = await registerUser('flow@example.com', 'SecurePass123!');
    
    if (!registerResult.success) {
        logError('Registration failed, cannot continue flow');
        return;
    }
    
    logStep(3, 'Authenticating the user');
    const authResult = await authenticateUser('flow@example.com', 'SecurePass123!');
    
    if (!authResult.success) {
        logError('Authentication failed');
        return;
    }
    
    logStep(4, 'Verifying the JWT token');
    const decoded = verifyToken(authResult.token);
    
    if (decoded) {
        logSuccess('Complete flow successful!');
        logInfo(`Authenticated user: ${decoded.email}`);
        logInfo(`Token expires: ${new Date(decoded.exp * 1000).toLocaleString()}`);
    } else {
        logError('Token verification failed');
    }
}

/**
 * Main demonstration function
 */
async function runDemonstration() {
    log('🚀 Secure Authentication System - Demonstration', 'bright');
    log('This demo shows all the features of the authentication system', 'blue');
    
    try {
        // Run all demonstrations
        await demonstrateEmailValidation();
        await demonstratePasswordValidation();
        
        const token = await demonstrateUserRegistration();
        await demonstrateUserAuthentication(token);
        await demonstrateTokenVerification(token);
        await demonstrateUserManagement();
        await demonstrateErrorHandling();
        await demonstrateCompleteFlow();
        
        logHeader('Demonstration Complete');
        logSuccess('All demonstrations completed successfully!');
        logInfo('The authentication system is working as expected.');
        logInfo('Check the README.md file for detailed documentation.');
        logInfo('Run "npm test" to execute the comprehensive test suite.');
        
    } catch (error) {
        logError(`Demonstration failed: ${error.message}`);
        console.error(error);
    }
}

// Run the demonstration if this file is executed directly
if (require.main === module) {
    runDemonstration();
}

module.exports = {
    runDemonstration,
    demonstrateEmailValidation,
    demonstratePasswordValidation,
    demonstrateUserRegistration,
    demonstrateUserAuthentication,
    demonstrateTokenVerification,
    demonstrateUserManagement,
    demonstrateErrorHandling,
    demonstrateCompleteFlow
}; 