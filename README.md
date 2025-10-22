# Collateral Service

A comprehensive collateral management service built with Spring Boot 3.5.6 and WebFlux, designed for use by Encumbrance Agents to manage collateral assets and their encumbrances. The service integrates with external Title Registry and Auto Valuation services for enhanced functionality.

## Features

- **Collateral Management**: Create, update, retrieve, and manage collateral assets
- **Encumbrance Tracking**: Full lifecycle management of encumbrances on collateral
- **External Title Registry Integration**: Verify titles, check ownership, and search existing encumbrances
- **Auto Valuation Service Integration**: Automated property valuation, market trends, and comparable property analysis
- **Reactive Architecture**: Built with Spring WebFlux for high-performance, non-blocking operations
- **Real-time Value Updates**: Automatic calculation of available and encumbered values
- **Comprehensive Status Management**: Track collateral and encumbrance status changes
- **RESTful APIs**: Well-designed REST endpoints for all operations

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring WebFlux** (Reactive)
- **Spring Data R2DBC** (Reactive Database Access)
- **H2 Database** (In-Memory Database)
- **OpenAPI 3 / Swagger UI** (API Documentation)
- **Lombok** (Code Generation)
- **Maven** (Build Tool)

## API Endpoints

### Collateral Management

#### Base URL: `/api/v1/collaterals`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create a new collateral |
| GET | `/{collateralId}` | Get collateral by ID |
| PUT | `/{collateralId}` | Update collateral |
| DELETE | `/{collateralId}` | Delete collateral |
| GET | `/customer/{customerId}` | Get all collaterals for a customer |
| GET | `/account/{accountId}` | Get all collaterals for an account |
| GET | `/status/{status}` | Get collaterals by status |
| GET | `/customer/{customerId}/available?minValue={amount}` | Get available collaterals for customer |
| GET | `/encumbered` | Get all encumbered collaterals |
| PATCH | `/{collateralId}/value` | Update collateral market value |
| GET | `/types` | Get all collateral types |
| GET | `/statuses` | Get all collateral statuses |

#### External API Integration Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/create-with-validation` | Create collateral with title verification and auto valuation |
| POST | `/{collateralId}/verify-title` | Verify title through external Title Registry |
| GET | `/{collateralId}/ownership/{titleNumber}` | Get ownership details from Title Registry |
| GET | `/{collateralId}/existing-encumbrances/{titleNumber}` | Search existing encumbrances in Title Registry |
| POST | `/{collateralId}/auto-valuation` | Request automated valuation |
| GET | `/{collateralId}/market-trends` | Get market trends for collateral location and type |
| GET | `/{collateralId}/comparable-properties` | Get comparable properties analysis |
| POST | `/{collateralId}/revaluation` | Request revaluation with specific reason |

### Encumbrance Management

#### Base URL: `/api/v1/encumbrances`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create a new encumbrance |
| GET | `/{encumbranceId}` | Get encumbrance by ID |
| PUT | `/{encumbranceId}` | Update encumbrance |
| DELETE | `/{encumbranceId}` | Delete encumbrance |
| GET | `/collateral/{collateralId}` | Get all encumbrances for a collateral |
| GET | `/collateral/{collateralId}/active` | Get active encumbrances for a collateral |
| GET | `/loan/{loanId}` | Get encumbrances for a loan |
| GET | `/customer/{customerId}` | Get encumbrances for a customer |
| GET | `/status/{status}` | Get encumbrances by status |
| GET | `/expired` | Get all expired encumbrances |
| GET | `/collateral/{collateralId}/total-amount` | Get total encumbered amount |
| PATCH | `/{encumbranceId}/release` | Release an encumbrance |
| PATCH | `/{encumbranceId}/partial-release` | Partially release an encumbrance |
| POST | `/expire-encumbrances` | Process expired encumbrances |
| GET | `/types` | Get all encumbrance types |
| GET | `/statuses` | Get all encumbrance statuses |

## Data Models

### Collateral

```json
{
  "collateralId": "COL-12345678",
  "customerId": "CUST-001",
  "accountId": "ACC-001",
  "type": "REAL_ESTATE",
  "description": "Residential Property",
  "estimatedValue": 500000.00,
  "marketValue": 480000.00,
  "currency": "USD",
  "status": "ACTIVE",
  "location": "123 Main St, City, State",
  "evaluationDate": "2024-01-15T10:30:00",
  "availableValue": 280000.00,
  "encumberedValue": 200000.00,
  "legalDescription": "Lot 1, Block 2, Subdivision XYZ",
  "ownershipDocuments": "Deed of Sale #12345",
  "lastInspectionDate": "2024-01-10T14:00:00",
  "riskRating": "LOW"
}
```

### Encumbrance

```json
{
  "encumbranceId": "ENC-87654321",
  "collateralId": "COL-12345678",
  "loanId": "LOAN-001",
  "customerId": "CUST-001",
  "amount": 200000.00,
  "currency": "USD",
  "type": "MORTGAGE",
  "status": "ACTIVE",
  "effectiveDate": "2024-01-01T00:00:00",
  "expiryDate": "2029-01-01T00:00:00",
  "description": "Primary mortgage lien",
  "priority": 1,
  "legalReference": "Mortgage #MOT-001",
  "notes": "Standard mortgage terms"
}
```

## Collateral Types

- `REAL_ESTATE` - Real Estate
- `VEHICLE` - Vehicle
- `EQUIPMENT` - Equipment
- `INVENTORY` - Inventory
- `SECURITIES` - Securities
- `CASH_DEPOSIT` - Cash Deposit
- `ACCOUNTS_RECEIVABLE` - Accounts Receivable
- `INTELLECTUAL_PROPERTY` - Intellectual Property
- `PRECIOUS_METALS` - Precious Metals
- `ARTWORK` - Artwork
- `JEWELRY` - Jewelry
- `OTHER` - Other

## Collateral Statuses

- `ACTIVE` - Active
- `PENDING_EVALUATION` - Pending Evaluation
- `UNDER_REVIEW` - Under Review
- `APPROVED` - Approved
- `REJECTED` - Rejected
- `ENCUMBERED` - Encumbered
- `PARTIALLY_ENCUMBERED` - Partially Encumbered
- `RELEASED` - Released
- `LIQUIDATED` - Liquidated
- `SUSPENDED` - Suspended
- `EXPIRED` - Expired
- `INACTIVE` - Inactive

## Encumbrance Types

- `MORTGAGE` - Mortgage
- `LIEN` - Lien
- `PLEDGE` - Pledge
- `SECURITY_INTEREST` - Security Interest
- `CHARGE` - Charge
- `HYPOTHECATION` - Hypothecation
- `ASSIGNMENT` - Assignment
- `GUARANTEE` - Guarantee
- `FLOATING_CHARGE` - Floating Charge
- `FIXED_CHARGE` - Fixed Charge
- `DEED_OF_TRUST` - Deed of Trust
- `OTHER` - Other

## Encumbrance Statuses

- `PENDING` - Pending
- `ACTIVE` - Active
- `RELEASED` - Released
- `PARTIALLY_RELEASED` - Partially Released
- `SUSPENDED` - Suspended
- `EXPIRED` - Expired
- `CANCELLED` - Cancelled
- `DEFAULTED` - Defaulted
- `UNDER_REVIEW` - Under Review
- `TRANSFERRED` - Transferred
- `MODIFIED` - Modified
- `TERMINATED` - Terminated

## Running the Application

1. **Prerequisites**: Java 17 or higher

2. **Build the application**:
   ```bash
   ./mvnw clean compile
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**:
   - Application runs on: `http://localhost:8081`
   - API Documentation (Swagger UI): `http://localhost:8081/webjars/swagger-ui/index.html`
   - Health check: `http://localhost:8081/actuator/health`
   - Application info: `http://localhost:8081/actuator/info`

## Key Features for Encumbrance Agents

### Automatic Value Management
- Automatic calculation of available collateral value
- Real-time updates when encumbrances are added/removed
- Status updates based on encumbrance levels

### Encumbrance Lifecycle
- Create encumbrances with priority ordering
- Partial and full release capabilities
- Automatic expiration processing
- Comprehensive audit trail

### Query Capabilities
- Find available collaterals by customer and minimum value
- Track all encumbrances by various criteria
- Monitor expired encumbrances
- Calculate total encumbered amounts

### Data Integrity
- Automatic recalculation of collateral values
- Consistent status management
- Foreign key relationships between collaterals and encumbrances

### External API Integration Features
- **Title Registry Integration**: Verify property titles, check ownership history, and search for existing encumbrances
- **Auto Valuation Service**: Get automated property valuations, market trend analysis, and comparable property data
- **Resilient Design**: Service continues to operate even if external APIs are unavailable
- **Configurable Timeouts**: Customizable timeout settings for external API calls
- **Automatic Fallbacks**: Graceful degradation when external services are unavailable

## External API Configuration

The service integrates with two external APIs:

### Title Registry API
- **Base URL**: `http://localhost:8081` (configurable via `external.title-registry.base-url`)
- **Timeout**: 10 seconds (configurable via `external.title-registry.timeout`)
- **Endpoints Used**:
  - `POST /api/v1/title/verify` - Verify title validity
  - `GET /api/v1/title/{titleNumber}/ownership` - Get ownership details
  - `GET /api/v1/title/{titleNumber}/encumbrances` - Search existing encumbrances

### Auto Valuation Service API
- **Base URL**: `http://localhost:8082` (configurable via `external.auto-valuation.base-url`)
- **Timeout**: 15 seconds (configurable via `external.auto-valuation.timeout`)
- **Endpoints Used**:
  - `POST /api/v1/valuation/request` - Request property valuation
  - `GET /api/v1/market-trends` - Get market trend data
  - `POST /api/v1/comparables/search` - Search comparable properties
  - `POST /api/v1/valuation/revalue` - Request revaluation

## API Documentation

The service includes comprehensive OpenAPI 3 documentation accessible via Swagger UI:

- **Swagger UI**: `http://localhost:8081/webjars/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`
- **Interactive API Testing**: All endpoints can be tested directly from the Swagger UI

### API Documentation Features

- **Comprehensive Endpoint Documentation**: All REST endpoints documented with descriptions, parameters, and response schemas
- **Interactive Testing**: Test API endpoints directly from the browser
- **Schema Documentation**: Complete data model documentation
- **Response Examples**: Sample request/response payloads
- **API Grouping**: Endpoints organized by functional areas (Collateral Management, Encumbrance Management, Auto Loan Valuation)

## Database Configuration

The service uses R2DBC with H2 in-memory database:

### Database Details
- **Database Type**: H2 In-Memory
- **Connection**: R2DBC reactive driver
- **URL**: `r2dbc:h2:mem:///collateral_db`
- **Schema Initialization**: Automatic via `schema.sql`
- **Sample Data**: Pre-loaded via `data.sql`

### Database Schema
- **Collateral Table**: Stores all collateral asset information with indexes
- **Encumbrance Table**: Stores encumbrance details with foreign key to collateral
- **Indexes**: Optimized queries with indexes on frequently accessed fields
- **Constraints**: Foreign key relationships and data integrity constraints

### Sample Data Included
- **5 Sample Collaterals**: Various types including real estate, vehicles, equipment, and inventory
- **3 Sample Encumbrances**: Different encumbrance types including mortgages and liens
- **Realistic Data**: Professional sample data for testing and demonstration

## Configuration

The service includes the following configuration (via `application.yml`):

### Server Configuration
- **Port**: 8081 (configurable)
- **Context Path**: `/` (root)

### Database Configuration
- **R2DBC URL**: `r2dbc:h2:mem:///collateral_db`
- **Auto Schema**: Enabled with `schema.sql`
- **Auto Data**: Enabled with `data.sql`

### Logging Configuration
- **Application Logging**: INFO level for application packages
- **R2DBC Logging**: DEBUG level for database operations
- **Console Pattern**: Formatted timestamp and message output

### Management Configuration
- **Actuator Endpoints**: Health, info, and metrics exposed
- **Health Details**: Always shown for detailed health information

### Security Configuration
- **CORS**: Removed (not needed for this implementation)
- **Authentication**: Not implemented (business requirement)

## Development Features

### Code Quality
- **No Test Classes**: Removed as per requirements
- **Clean Architecture**: Separation of concerns with repository, service, and controller layers
- **Reactive Programming**: Full reactive stack using Project Reactor
- **Lombok Integration**: Reduced boilerplate code

### Performance Optimizations
- **Database Indexes**: Strategic indexing for query performance
- **Connection Pooling**: R2DBC connection management
- **Reactive Streams**: Non-blocking I/O operations

## Notes

- **Database Persistence**: Uses R2DBC for reactive database operations with H2 in-memory database
- **Data Precision**: All monetary values use `BigDecimal` for precision
- **Date Handling**: Timestamps use `LocalDateTime` format
- **ID Generation**: Service generates unique IDs for collaterals (COL-) and encumbrances (ENC-)
- **Configuration Format**: Uses YAML configuration instead of properties
- **API Documentation**: Complete OpenAPI 3 specification with Swagger UI
- **Test Removal**: No test classes included as per requirements
- **Cross-Origin**: CORS configuration removed as not needed
- **External APIs**: Integrated with Title Registry and Auto Valuation services
- **Data Integrity**: Automatic calculation of available/encumbered values
- **Status Management**: Comprehensive status tracking for both collaterals and encumbrances
