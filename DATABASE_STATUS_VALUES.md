# User Status Values - Database Documentation

## Status Field in Users Table

The `users` table has a `status` field of type `INTEGER` that tracks the approval/block status of users.

### Status Values

| Status Code | Status Name | Description |
|------------|-------------|-------------|
| 0 | **Pending** | User registration is pending admin approval |
| 1 | **Approved** | User has been approved and can log in |
| 2 | **Rejected** | User registration has been rejected |
| 3 | **Blocked** | User account has been blocked by admin (cannot log in) |

### Status Flow

```
Registration → Status 0 (Pending)
    ↓
Admin Approval → Status 1 (Approved) ✅ Can login
    OR
Admin Rejection → Status 2 (Rejected) ❌ Cannot login
    OR
Admin Block → Status 3 (Blocked) ❌ Cannot login
```

### Implementation Details

1. **Blocking a User**: Sets status to `3` (Blocked)
   - User cannot log in
   - User sees blocked message in dashboard
   - User is logged out automatically

2. **Unblocking a User**: Sets status to `1` (Approved)
   - User can log in again
   - User regains access to dashboard

3. **Deleting a Blocked Teacher**:
   - If teacher has subjects/assignments, must be blocked first (status = 3)
   - When blocked teacher is deleted, all related subjects and assignments are cascade deleted

### Database Schema

```sql
CREATE TABLE IF NOT EXISTS users (
    ...
    status INTEGER, -- 0 = Pending, 1 = Approved, 2 = Rejected, 3 = Blocked
    ...
);
```

### API Endpoints

- `PATCH /api/v1/users/{id}/block` - Block a user (sets status to 3)
- `PATCH /api/v1/users/{id}/unblock` - Unblock a user (sets status to 1)
- `DELETE /api/v1/users/{id}/delete_user` - Delete user (requires blocking if teacher has subjects/assignments)

### Notes

- Status `3` (Blocked) was added to support admin blocking functionality
- Blocked users are prevented from logging in at the authentication level
- Blocked users see a blocking dialog in their dashboard if they're already logged in
- The status field is an INTEGER, so it can support additional status values in the future if needed

