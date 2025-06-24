# AI Documentation & Code Style Review: JavaScript Utility Function

## AI Prompt
```
Please review the following JavaScript utility function for documentation and code style. Identify any missing JSDoc comments, unclear parameter/return types, or inconsistent formatting. Suggest and show improvements.

@src/utils/formatDate.js
```

## AI Findings
- **Documentation:**
  - No JSDoc comment describing the function, its parameters, or return value.
  - No usage example.
- **Code Style:**
  - Function name and parameter are clear.
  - Formatting is consistent.
- **Suggestions:**
  - Add a JSDoc comment with parameter and return type descriptions.
  - Provide a usage example in the documentation.

## Example Improvement: Add JSDoc and Usage Example

### Before
```js
function formatDate(date) {
  return date.toISOString().split('T')[0];
}
```

### After
```js
/**
 * Formats a Date object as a YYYY-MM-DD string.
 *
 * @param {Date} date - The date to format
 * @returns {string} The formatted date string (YYYY-MM-DD)
 *
 * @example
 * formatDate(new Date('2024-05-01')) // '2024-05-01'
 */
function formatDate(date) {
  return date.toISOString().split('T')[0];
}
```

---

**Summary:**
- Adding JSDoc and a usage example improves the discoverability and usability of the utility function for other developers. 