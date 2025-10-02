# petCONNECT - Advanced Pet Adoption Platform

![petCONNECT Logo](https://via.placeholder.com/400x100/667eea/ffffff?text=petCONNECT)

## 🐾 Overview

**petCONNECT** is a modern, intelligent pet adoption platform built with Java Spring Boot. It features AI-powered pet matching, dynamic shelter management, and a beautiful, responsive user interface designed to connect loving pets with their perfect families.

### ✨ Key Features

- **🤖 AI-Powered Pet Matching**: Advanced algorithm that matches pets with adopters based on lifestyle compatibility
- **🏠 Dynamic Shelter Dashboard**: Real-time application management with HTMX for seamless updates
- **🔐 Role-Based Security**: Secure authentication with different access levels (Admin, Shelter Admin, Adopter)
- **📱 Responsive Design**: Beautiful, mobile-first UI built with Bootstrap 5
- **🐳 Docker Ready**: Fully containerized for easy deployment
- **🛡️ Robust Architecture**: Built with Spring Boot, Spring Security, and PostgreSQL

## 🏗️ Technology Stack

### Backend
- **Java 17** - Modern Java features and performance
- **Spring Boot 3.2** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction
- **PostgreSQL** - Primary database
- **Maven** - Dependency management

### Frontend
- **Thymeleaf** - Server-side templating
- **Bootstrap 5** - CSS framework
- **HTMX** - Dynamic interactions without complex JavaScript
- **Font Awesome** - Icons

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## 🚀 Quick Start

### Prerequisites

Make sure you have the following installed:
- **Java 17** or higher ([Download](https://adoptium.net/))
- **Docker** and **Docker Compose** ([Download](https://www.docker.com/products/docker-desktop/))
- **Git** ([Download](https://git-scm.com/))

### Option 1: Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/petconnect.git
   cd petconnect
   ```

2. **Start the application**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Application: http://localhost:8080
   - Database Admin (optional): http://localhost:5050

4. **Login with demo accounts**
   - **Admin**: `admin` / `password`
   - **Shelter Admin**: `shelter1` / `password`
   - **Adopter**: `adopter1` / `password`

### Option 2: Local Development

1. **Clone and setup database**
   ```bash
   git clone https://github.com/yourusername/petconnect.git
   cd petconnect
   
   # Start only PostgreSQL
   docker-compose up -d postgres
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**
   - Application: http://localhost:8080

## 📋 Project Structure

```
petconnect/
├── src/main/java/com/petconnect/project/
│   ├── config/              # Configuration classes
│   ├── controller/          # Web controllers
│   ├── entity/              # JPA entities
│   ├── exception/           # Custom exceptions
│   ├── repository/          # Data repositories
│   └── service/             # Business logic
├── src/main/resources/
│   ├── templates/           # Thymeleaf templates
│   ├── static/              # Static resources
│   ├── schema.sql           # Database schema
│   └── application*.properties
├── docker-compose.yml       # Docker orchestration
├── Dockerfile              # Application container
└── README.md               # This file
```

## 🎯 Core Features

### 1. AI-Powered Pet Matching

The heart of petCONNECT is its sophisticated matching algorithm that considers:

- **Energy Level Compatibility** (25 points)
- **Living Situation** (15 points)
- **Experience Level vs Trainability** (15 points)
- **Size Preference** (15 points)
- **Budget Compatibility** (10 points)
- **Social Compatibility** (10 points)
- **Age Preference** (10 points)

**How it works:**
1. Users complete a comprehensive lifestyle questionnaire
2. The algorithm calculates compatibility scores for all available pets
3. Results are ranked and presented with detailed explanations

### 2. Dynamic Shelter Dashboard

Built with HTMX for real-time updates without page refreshes:

- **Application Management**: Update application status with instant UI updates
- **Pet Management**: Add, edit, and manage pet profiles
- **Real-time Statistics**: Live dashboard with key metrics
- **Interactive Tables**: Sort, filter, and manage applications seamlessly

### 3. Security & User Management

- **Role-based Access Control**: Three user types with appropriate permissions
- **Secure Authentication**: BCrypt password hashing and session management
- **Protected Endpoints**: Route-level security based on user roles
- **Custom Success Handlers**: Role-based redirects after login

## 🔧 Configuration

### Environment Variables

For Docker deployment, you can override these environment variables:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/petconnect
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Application
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8080

# Logging
LOGGING_LEVEL_COM_PETCONNECT_PROJECT=INFO
```

### Database Schema

The application uses a well-normalized PostgreSQL schema with:

- **Users** - Authentication and profile information
- **Shelters** - Shelter information and admin relationships
- **Pets** - Pet profiles with detailed attributes
- **Lifestyle Profiles** - User preferences for matching
- **Personality Profiles** - Pet characteristics for matching
- **Adoption Applications** - Application workflow management
- **Community Posts** - Social features (extensible)

## 🧪 Testing

### Demo Data

The application includes sample data for testing:

- **3 Demo Users** (Admin, Shelter Admin, Adopter)
- **Sample Pets** with personality profiles
- **Test Applications** in various states

### User Roles

1. **ADMIN**
   - Full system access
   - User management
   - System configuration

2. **SHELTER_ADMIN**
   - Manage shelter pets
   - Process adoption applications
   - View shelter analytics

3. **ADOPTER**
   - Complete matching questionnaire
   - View pet matches
   - Submit adoption applications

## 🚀 Deployment

### Production Deployment

1. **Update configuration**
   ```bash
   # Create production environment file
   cp docker-compose.yml docker-compose.prod.yml
   # Edit database passwords and other sensitive data
   ```

2. **Deploy with Docker**
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

3. **Setup reverse proxy** (recommended)
   - Use Nginx or Apache for SSL termination
   - Configure domain and SSL certificates

### Scaling Considerations

- **Database**: Use managed PostgreSQL service (AWS RDS, Google Cloud SQL)
- **Application**: Scale horizontally with load balancer
- **Storage**: Use cloud storage for pet images
- **Caching**: Add Redis for session storage and caching

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 API Documentation

### Key Endpoints

- `GET /` - Home page
- `GET /login` - Login page
- `POST /register` - User registration
- `GET /matching/questionnaire` - Pet matching questionnaire
- `GET /matching/results` - View pet matches
- `GET /shelter/dashboard` - Shelter admin dashboard
- `POST /shelter/applications/update/{id}` - Update application status (HTMX)

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check if PostgreSQL is running
   docker-compose ps postgres
   
   # View logs
   docker-compose logs postgres
   ```

2. **Application Won't Start**
   ```bash
   # Check application logs
   docker-compose logs petconnect-app
   
   # Verify Java version
   java --version
   ```

3. **Port Already in Use**
   ```bash
   # Change port in docker-compose.yml
   ports:
     - "8081:8080"  # Use port 8081 instead
   ```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot Team** - For the amazing framework
- **Bootstrap Team** - For the beautiful UI components
- **HTMX Team** - For making dynamic UIs simple
- **PostgreSQL Team** - For the robust database
- **All Pet Shelters** - For the inspiration to build this platform

---

**Made with ❤️ for pets and their future families**

For questions or support, please open an issue or contact the development team.
#   p e t C O N N E C T  
 