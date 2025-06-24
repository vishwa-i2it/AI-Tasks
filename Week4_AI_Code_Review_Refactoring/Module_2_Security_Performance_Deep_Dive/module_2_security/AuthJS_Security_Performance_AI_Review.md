# AI Security & Performance Review: auth.js (Node.js)

## AI Prompt
```
Please review the following Node.js authentication logic for security vulnerabilities and performance issues. Identify any risks (e.g., hardcoded secrets, lack of rate limiting, sync operations, etc.) and suggest specific improvements.

@src/auth.js
```

## AI Findings
- **Security Issues:**
  - JWT secret is hardcoded in the source code; should be loaded from environment variables.
  - No rate limiting on authentication endpoints; vulnerable to brute-force attacks.
  - No account lockout after repeated failed attempts.
- **Performance Issues:**
  - Uses synchronous bcrypt operations, which can block the event loop under load.
- **Best Practices:**
  - Use async versions of bcrypt functions.
  - Store secrets in environment variables, not in code.
  - Implement rate limiting and account lockout.

## Suggested Improvements
1. **Move JWT Secret to Environment Variable:**
   - Use `process.env.JWT_SECRET` and fail if not set.
2. **Use Async Bcrypt Functions:**
   - Replace `bcrypt.compareSync` and `bcrypt.hashSync` with their async counterparts.
3. **Add Rate Limiting:**
   - Use a middleware (e.g., express-rate-limit) to limit login attempts.
4. **Implement Account Lockout:**
   - Lock accounts after repeated failed login attempts.

## Example Improvement: Move JWT Secret to Environment Variable

### Before
```js
const JWT_SECRET = 'your-super-secret-jwt-key-change-in-production';
```

### After
```js
const JWT_SECRET = process.env.JWT_SECRET;
if (!JWT_SECRET) {
  throw new Error('JWT_SECRET environment variable is not set!');
}
```

---

**Summary:**
- The AI review identified critical security and performance issues. Moving secrets to environment variables and using async bcrypt functions are essential for production security and scalability. 