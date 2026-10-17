# Test plan and execution record

These checks use temporary QA accounts in a local MySQL database. The temporary records were removed after each run.

| # | Scenario | Expected result | Result |
|---:|---|---|---|
| 1 | Register a new student | Account is created with a salted password hash | Pass |
| 2 | Register the same email again | Unique email constraint rejects the duplicate | Pass |
| 3 | Log in with the correct password | Login succeeds for the matching student | Pass |
| 4 | Log in with an incorrect password | Login fails and returns no student ID | Pass |
| 5 | Add teaching and learning skills | Both skill types are stored for their owners | Pass |
| 6 | Match a learner to a teacher using different letter case | Matching returns the other student who teaches that skill | Pass |
| 7 | Send a request for a skill the receiver teaches | A pending exchange request is stored | Pass |
| 8 | View requests as the receiver | The incoming request, sender, skill, and status are shown | Pass |
| 9 | Respond as an unrelated student, then as the receiver | Unrelated student cannot respond; receiver can accept | Pass |
| 10 | Schedule a session for an accepted exchange | Session is saved and visible to the exchange partner | Pass |
| 11 | Submit a review with a rating outside 1–5 | Review is rejected and no row is stored | Pass |
| 12 | Submit and view a valid review | Review is saved and visible to the partner | Pass |

Additional checks passed: the complete menu workflow from registration through review and logout; successful login upgraded a legacy plain-text password to a salted hash; Java compilation with `-Xlint:all` completed without warnings.

The local database counts after test cleanup matched their initial values: 3 students, 6 skills, 0 exchanges, 0 sessions, and 0 reviews.
