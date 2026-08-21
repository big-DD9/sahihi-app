# Sahihi

**A full-stack savings and loans platform, built for a SACCO-style banking use case.**

Members save, request loans against their savings, and repay over time. Admins review and approve loan requests. Every balance change is logged as an auditable transaction — deposits, withdrawals, loan disbursements, and repayments all leave a permanent, timestamped trail, the same way a real SACCO passbook would.

**Live demo:** [sahihi-app.vercel.app](https://sahihi-app.vercel.app)
**API:** [sahihi-backend.onrender.com](https://sahihi-backend.onrender.com)

> Note: both services run on free-tier hosting, so the backend may take 30–60 seconds to wake up on the first request after a period of inactivity.

---

## Why it's built this way

Sahihi means "signature" or "genuine" in Swahili — historically, SACCO passbooks were verified with an ink stamp and signature on every entry. That idea shaped two real decisions in this project, not just the visual design:

- **Every transaction is logged, never overwritten.** Deposits, withdrawals, loan disbursements, and repayments each create a permanent `Transaction` row with a balance snapshot — the balance itself is never just updated in place without a trace.
- **Loan eligibility is enforced server-side, not trusted from the client.** A member can request up to 3x their current savings balance; that check lives in `LoanService`, so it can't be bypassed by a modified frontend request.

## Architecture

```
React (Vercel)  ──HTTPS/JSON──>  Spring Boot API (Render)  ──JDBC──>  PostgreSQL (Render)
                                        │
                                  JWT auth + role-based
                                  access control (Spring Security)
```

- **Frontend** calls the backend over a REST API, with a JWT attached to every authenticated request.
- **Backend** validates the JWT on every request via a custom filter, resolves the current user, and enforces role checks (`MEMBER` vs `ADMIN`) at the method level with `@PreAuthorize`.
- **Database** holds users, accounts, transactions, and loans, with `BigDecimal` used throughout for all monetary values to avoid floating-point rounding errors.

## Features

**Members**
- Register / log in with JWT-based authentication
- View savings balance and full transaction history
- Deposit and withdraw funds
- Request a loan (up to 3x current savings balance)
- Track loan status and make repayments against an approved loan

**Admins**
- Review all pending loan requests
- Approve (with a set interest rate) or reject each request
- Approved loans disburse automatically into the member's account and log a transaction

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3, Spring Security, Spring Data JPA |
| Auth | JWT (jjwt), BCrypt password hashing, role-based access control |
| Database | PostgreSQL |
| Frontend | React 18, Vite, React Router |
| Styling | Custom design system (CSS variables, no framework) |
| Containerization | Docker, multi-stage builds for both services |
| Hosting | Render (backend + database), Vercel (frontend) |

## Running locally

**Backend** (requires Java 17, Maven, and a local PostgreSQL instance):
```bash
cd backend
mvn spring-boot:run
```
Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` as environment variables, or edit the defaults in `application.properties`.

**Frontend** (requires Node 18+):
```bash
cd frontend
npm install
npm run dev
```
The dev server proxies API calls to `http://localhost:8080` automatically.

## What I'd add with more time

- **In-app notifications** — members currently have to check the loans page manually to see if a request was decided; there's no push alert yet.
- **Loan repayment schedules** — right now repayment is a single lump-sum action rather than an amortized monthly schedule.
- **Password reset flow** — there's currently no recovery path if a member forgets their password.
- **CI/CD pipeline** — automated testing and deployment via GitHub Actions.

## Project structure

```
sahihi/
├── backend/     Spring Boot REST API
└── frontend/    React single-page app
```

---

Built by [Solomon Ndege](https://github.com/big-DD9) as a portfolio project.
