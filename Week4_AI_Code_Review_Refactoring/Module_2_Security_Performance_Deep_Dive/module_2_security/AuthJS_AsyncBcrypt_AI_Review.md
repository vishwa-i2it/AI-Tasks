# AI Performance Review: Use Async Bcrypt in Auth API (Node.js)

## AI Prompt
```
Please review the following Node.js authentication logic for performance. Identify any blocking operations and suggest how to use async bcrypt functions for password hashing and comparison. Show a code example.

@src/auth.js
```

## AI Findings
- **Performance Issue:**
  - Uses synchronous bcrypt functions (`bcrypt.hashSync`, `bcrypt.compareSync`), which block the event loop and can degrade performance under load.
- **Best Practice:**
  - Use async versions (`bcrypt.hash`, `bcrypt.compare`) to avoid blocking the event loop.

## Suggested Improvement: Use Async Bcrypt Functions

### Before
```js
const bcrypt = require('bcrypt');

const hashed = bcrypt.hashSync(password, 12);
const isMatch = bcrypt.compareSync(password, hashed);
```

### After
```js
const bcrypt = require('bcrypt');

const hashed = await bcrypt.hash(password, 12);
const isMatch = await bcrypt.compare(password, hashed);
```

---

**Summary:**
- Using async bcrypt functions improves the scalability and responsiveness of the authentication API, especially under concurrent load. 