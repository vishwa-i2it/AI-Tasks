# AI Security Review: Add Rate Limiting to Auth API (Node.js)

## AI Prompt
```
Please review the following Node.js authentication API for protection against brute-force attacks. Suggest how to add rate limiting to the login endpoint and show a code example.

@src/auth.js
```

## AI Findings
- **Vulnerability:**
  - The login endpoint has no rate limiting, making it vulnerable to brute-force attacks.
- **Best Practice:**
  - Use a rate limiting middleware (e.g., express-rate-limit) to restrict the number of login attempts per IP.

## Suggested Improvement: Add Rate Limiting Middleware
1. Install express-rate-limit:
   ```bash
   npm install express-rate-limit
   ```
2. Apply the middleware to the login endpoint.

### Before
```js
app.post('/api/auth/login', async (req, res) => {
  // ... authentication logic ...
});
```

### After
```js
const rateLimit = require('express-rate-limit');

const loginLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // limit each IP to 5 requests per windowMs
  message: 'Too many login attempts. Please try again later.'
});

app.post('/api/auth/login', loginLimiter, async (req, res) => {
  // ... authentication logic ...
});
```

---

**Summary:**
- Adding rate limiting to the login endpoint helps prevent brute-force attacks and improves the security posture of the authentication API. 