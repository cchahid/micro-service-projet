# 🎯 STEP-BY-STEP EXECUTION GUIDE - COMPLETE FRONTEND SETUP

## 📋 QUICK REFERENCE

**What you need:**
- ✅ Node.js 18+ (if not installed, get it from nodejs.org)
- ✅ Angular CLI
- ✅ All your backend services running
- ✅ Docker containers running (Kafka, DB, etc.)

**Time required:** ~20 minutes

---

## 🚀 STEP 1: VERIFY BACKEND IS RUNNING

```powershell
# Check Docker containers
docker-compose ps

# Should show all containers "Up"
```

Start everything if needed:
```powershell
cd C:\Users\chahid\IdeaProjects\micro-service-projet
docker-compose up -d
Start-Sleep -Seconds 30
```

Verify Eureka Dashboard:
```
http://localhost:8761
```

---

## 🛠️ STEP 2: CREATE ANGULAR PROJECT

```powershell
# Navigate to project root
cd C:\Users\chahid\IdeaProjects\micro-service-projet

# Create Angular project
ng new frontend --routing --style=scss --strict

# This will take 2-3 minutes...
```

When prompted: Press Enter for defaults or 'y' for routing.

---

## 📦 STEP 3: INSTALL DEPENDENCIES

```powershell
# Navigate to frontend
cd frontend

# Install all packages
npm install @angular/material @angular/cdk bootstrap socket.io-client jwt-decode

# This takes 2-3 minutes...
```

---

## 📁 STEP 4: CREATE FOLDER STRUCTURE

```powershell
# Create directories
mkdir -p src/app/models
mkdir -p src/app/services
mkdir -p src/app/components
mkdir -p src/app/guards
mkdir -p src/app/interceptors
```

---

## 💾 STEP 5: COPY CODE FILES

### From FRONTEND_COMPLETE_CODE.md, copy these files:

#### **Models:**
Create these files in `src/app/models/`:
- `dinner.ts`
- `reservation.ts`
- `review.ts`
- `user.ts`
- `notification.ts`

#### **Services:**
Create these files in `src/app/services/`:
- `dinner.service.ts`
- `reservation.service.ts`
- `auth.service.ts`
- `review.service.ts`
- `notification.service.ts`

#### **Guards:**
Create in `src/app/guards/`:
- `auth.guard.ts`

#### **Interceptors:**
Create in `src/app/interceptors/`:
- `auth.interceptor.ts`

#### **Components:**
Generate or create in `src/app/components/`:
- `navbar/`
- `home/`
- `dinner-list/`
- `dinner-detail/`
- `reservation-create/`
- `my-reservations/`
- `login/`
- `profile/`

Each component needs:
- `component-name.component.ts`
- `component-name.component.html`
- `component-name.component.scss`

#### **Update Root Files:**
Replace these in `src/app/`:
- `app.module.ts`
- `app-routing.module.ts`
- `app.component.html`
- `app.component.scss`

#### **Update Global Files:**
Replace:
- `src/styles.scss`
- `src/environments/environment.ts`
- `src/environments/environment.prod.ts`

---

## 🔧 STEP 6: ADD CORS TO BACKEND

For each microservice, add to the main class:

**reservationService/src/main/java/com/buberdinner/reservationservice/ReservationServiceApplication.java:**

```java
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ReservationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
```

**Repeat for:**
- dinnerService
- userService
- notificationService
- api-gateway (optional)

Then **rebuild each service:**
```powershell
cd reservationService
mvn clean install -DskipTests

cd ../dinnerService
mvn clean install -DskipTests

cd ../userService
mvn clean install -DskipTests

cd ../notificationService
mvn clean install -DskipTests
```

---

## 🚀 STEP 7: START THE FRONTEND

```powershell
# Make sure you're in frontend directory
cd C:\Users\chahid\IdeaProjects\micro-service-projet\frontend

# Start dev server
ng serve --open
```

This will:
- Compile the Angular app
- Start dev server on port 4200
- Open browser automatically

**Wait 30 seconds for compilation...**

---

## ✅ STEP 8: VERIFY IT'S WORKING

Check these in your browser:

1. **Home Page** (should load with featured dinners)
   ```
   http://localhost:4200
   ```

2. **Dinners List** (should show all dinners)
   ```
   http://localhost:4200/dinners
   ```

3. **Eureka Dashboard** (should show all services registered)
   ```
   http://localhost:8761
   ```

4. **MailDev** (to see email notifications)
   ```
   http://localhost:1080
   ```

---

## 🧪 STEP 9: FULL FUNCTIONAL TEST

Follow this flow to test everything:

### 1. **Browse Dinners**
```
→ Visit http://localhost:4200/dinners
→ Should see list of dinners with search/filter
→ No login required
```

### 2. **View Dinner Details**
```
→ Click on any dinner
→ Should see full details, reviews, images
→ Should see "Make a Reservation" button
```

### 3. **Try Login**
```
→ Click "Login" in navbar
→ Try any username/password (adjust in your User Service if needed)
→ Should redirect to home after login
```

### 4. **Create Reservation**
```
→ After login, click "View & Book" on a dinner
→ Should go to reservation form
→ Fill in date/time and restaurant name
→ Click "Create Reservation"
→ Should see success message
→ Check email at http://localhost:1080 (should see confirmation email from Kafka)
```

### 5. **View My Reservations**
```
→ Click "My Reservations" in navbar
→ Should see your created reservation
→ Can cancel it from here
```

### 6. **Add Review**
```
→ Go back to dinner detail
→ Click "Add Review"
→ Give rating and comment
→ Submit
→ Review should appear immediately
```

### 7. **View Profile**
```
→ Click "Profile" in navbar
→ Should see your user information
```

---

## 🐛 TROUBLESHOOTING

### **Problem: Can't access http://localhost:4200**
```
Solution:
- Make sure ng serve is still running
- Check terminal for compilation errors
- Try: ng serve --open
```

### **Problem: dinners not loading**
```
Solution:
- Check if backend is running: docker-compose ps
- Check API Gateway: http://localhost:8080/api/dinners
- Check browser console (F12) for errors
- Verify CORS is enabled on dinnerService
```

### **Problem: Login fails**
```
Solution:
- Make sure userService is running
- Check if it's registered in Eureka
- Verify user exists in database
- Check browser console for error details
```

### **Problem: Can't create reservation**
```
Solution:
- Make sure you're logged in
- Check if reservationService is running
- Check Eureka dashboard
- Verify CORS is enabled
- Check browser console for error message
```

### **Problem: No email notifications**
```
Solution:
- Check if Kafka is running: docker ps | findstr kafka
- Check if Notification Service is running
- Go to http://localhost:1080 to see emails
- Check notification service logs
```

### **Problem: "Module not found" errors**
```
Solution:
- Delete node_modules: rm -r node_modules
- Delete package-lock.json: rm package-lock.json
- Reinstall: npm install
- Reinstall additional: npm install @angular/material @angular/cdk bootstrap socket.io-client jwt-decode
```

### **Problem: CORS errors**
```
Solution:
- Check if CORS code is added to all services
- Restart services: docker-compose down && docker-compose up -d
- Clear browser cache (Ctrl+Shift+Delete)
- Try incognito mode
```

---

## 🔍 USEFUL COMMANDS

```powershell
# Check all containers running
docker-compose ps

# View logs of a service
docker logs reservationService --tail 50

# Restart all services
docker-compose restart

# Clean and rebuild Angular
rm -r node_modules dist
npm install
npm install @angular/material @angular/cdk bootstrap socket.io-client jwt-decode

# Rebuild backend services
cd reservationService && mvn clean install -DskipTests && cd ..

# Kill process on port if needed
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# Check if services are registered
curl http://localhost:8761/eureka/apps

# View Kafka topics
docker exec -it ms_kafka kafka-topics --list --bootstrap-server localhost:9092
```

---

## 📋 FINAL CHECKLIST

Before considering it "done":

- [ ] Angular project created
- [ ] Dependencies installed
- [ ] All code files copied
- [ ] CORS added to backend services
- [ ] Backend services rebuilt
- [ ] Docker containers running
- [ ] Frontend running (ng serve)
- [ ] Can browse dinners without login
- [ ] Can login with credentials
- [ ] Can create reservation
- [ ] Email notification received
- [ ] Can cancel reservation
- [ ] Can add review
- [ ] All components responsive on mobile

---

## 🎉 SUCCESS INDICATORS

Your setup is complete when:

✅ Frontend opens without errors at http://localhost:4200
✅ Dinners list loads and displays properly
✅ Can login successfully
✅ Can create reservation
✅ Email notification appears at http://localhost:1080
✅ Can view and manage reservations
✅ All pages are responsive
✅ No console errors (F12 to check)

---

## 📞 IF SOMETHING GOES WRONG

1. **Check browser console (F12)** - error messages are there
2. **Check terminal where ng serve is running** - compilation errors
3. **Check Docker logs:**
   ```powershell
   docker logs <service-name> --tail 100
   ```
4. **Check network requests (F12 → Network tab)**
   - Verify API calls go to http://localhost:8080/api
   - Check response status codes (should be 200, 201, etc.)

---

## 🚀 YOU'RE DONE!

Once everything is working, you have:

✅ **Full-stack microservices application**
✅ **Beautiful Angular frontend**
✅ **Event-driven email notifications**
✅ **Real-time reservation system**
✅ **Complete review system**
✅ **User authentication**
✅ **Responsive design**

### Now you can:
- Deploy to production
- Add more features
- Scale the services
- Add more users
- Customize the design
- Integrate with payment systems
- Add analytics

**Congratulations! 🎊**


