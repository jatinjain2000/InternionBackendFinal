package com.example.pdfcreator.controller;
import com.example.pdfcreator.entity.LoginEntity;
import com.example.pdfcreator.entity.QrUserEntity;
import com.example.pdfcreator.entity.ResidenceCertificateForm;
import com.example.pdfcreator.entity.UserEntity;
import com.example.pdfcreator.repo.UserRepo;
import com.example.pdfcreator.service.CustomUserDetailService;
import com.example.pdfcreator.service.TwoFactorAuthService;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;



//
//import com.lowagie.text.Document;
//import com.lowagie.text.Paragraph;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;
//
//@RestController
//public class PdfController {
//
//    @GetMapping("/generate")
//    public String generatePdf() {
//        try {
//            // Save the document to a temporary file with ".pdf" extension
//            File tempFile = File.createTempFile("output", ".pdf");
//            Document document = new Document();
//
//            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                // Open the document before writing to it
//                document.open();
//                // Add the content to the document
//                document.add(new Paragraph("Hello, this is a sample PDF content."));
//            }
//
//            // Close the document after saving
//            document.close();
//
//            // Specify the folder where you want to save the PDF
//            String folderPath = "C:\\Users\\Priyanshu\\OneDrive\\Desktop\\Training\\pdfcreator\\src\\main\\java\\com\\example\\pdfcreator\\datafolder";
//
//            // Create the folder if it doesn't exist
//            Path folder = Files.createDirectories(Path.of(folderPath));
//
//            // Move the temporary file to the desired folder
//            Path destination = folder.resolve("output.pdf");
//            Files.move(tempFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
//
//            return "PDF successfully generated and saved in the folder.";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error generating or saving the PDF.";
//        }
//    }
//}
@RestController
@CrossOrigin("*")
@RequestMapping(path = "/api")
public class PdfController {
    private Map<String, String> userCaptchaMap = new HashMap<>();

    private String storedCaptchaText;
    @Autowired
    CustomUserDetailService customUserDetailService;
    @Autowired
    TwoFactorAuthService twoFactorAuthService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepo userRepo;
    @GetMapping("/generate")
    public String generatePdf() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("C:\\\\Users\\\\Priyanshu\\\\OneDrive\\\\Desktop\\\\Training\\\\pdfcreator\\\\src\\\\main\\\\java\\\\com\\\\example\\\\pdfcreator\\\\datafolder\\\\iTextHelloWorld.pdf"));
        }catch (Exception e){
            return "not added";
        };
        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("Hello World", font);
        try {
            document.add(chunk);
        }catch (Exception e){
            return "not added";
        }
        document.close();
        return "added success";
    }

    @PostMapping("/register")
    public String userRegistration(@RequestBody UserEntity user) throws Exception {
        user.setVerify_email(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        customUserDetailService.saveUserDetails(user);
        return "success";
    }
    @GetMapping("/register/otp/{username}/{otp}")
    public Boolean checkOtp(@PathVariable("username") String username,
                            @PathVariable("otp") String otp) {
        UserEntity dummy = userRepo.findByEmail(username);

        return twoFactorAuthService.isOtpValid(dummy.getSecret_key(),otp);
    }
    @GetMapping("/register/verified/{username}")
    public String verifyRegistration(@PathVariable(name = "username") String username){
        UserEntity temp=userRepo.findByEmail(username);
        temp.setVerify_email(true);
        userRepo.save(temp);
        return "success";
    }
    @GetMapping("/register/generateQr")
    public String generateQr(@RequestParam String email){

        UserEntity temp=userRepo.findByEmail(email);

        return twoFactorAuthService.generateQrCodeImageUri(temp.getSecret_key());
    }
//    @PostMapping("/register/validateOtp")
//    public Boolean checkOtp(@RequestParam String email, @RequestParam String otp){
//
//        UserEntity temp=userRepo.findByEmail(email);
//        return twoFactorAuthService.isOtpValid(temp.getSecret_key(),otp);
//    }
//    @GetMapping("/register/login")
//    public String loginUser(@RequestBody LoginEntity loginEntity) {
//        UserEntity temp = userRepo.findByEmail(loginEntity.getEmail());
//        UserEntity temp2 = userRepo.findByPassword(loginEntity.getPassword());
//        UserEntity temp3 = userRepo.findByEmail(loginEntity.getEmail());
//        if (temp == null) {
//            return new String("User not found");
//        } else if (temp2 == null) {
//            return new String("User is not login with specified Password");
//        } else if (temp3 == null) {
//            return new String("user is not registered with specified email");
//        } else if (temp3.getVerify_email()==false) {
//            return "Please verify your email.";
//        } else {
//            return "Success";
//        }
//    }
//@PostMapping("/login/{randomId}")
//public User getUserByEmailAndPassword(@PathVariable("randomId") int randomId, @RequestBody User user) {
//    String code = hm.get(randomId);
//
//    if (user != null && user.getCaptcha() != null && user.getCaptcha().equals(code)) {
//        return userService.getUserByEmailAndPassword(user.getEmail(), user.getPassword());
//    }
//
//    return null;
//}
    @PostMapping("/residenceform")
    public String getResidenceForm(@RequestBody ResidenceCertificateForm certificateForm){
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("C:\\\\Users\\\\Priyanshu\\\\OneDrive\\\\Desktop\\\\Training\\\\pdfcreator\\\\src\\\\main\\\\java\\\\com\\\\example\\\\pdfcreator\\\\datafolder\\\\iTextHelloWorld.pdf"));
        }catch (Exception e){
            return "not added";
        };
        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Residence Certification Form", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        Font contentFont = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 12, BaseColor.BLACK);
        Paragraph content=new Paragraph("This is to certify that Sri/Smt -",contentFont);
        Font boldFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, Font.BOLD, BaseColor.BLACK);
        Chunk boldChunk = new Chunk("Priyanshu Lodha", boldFont);
        content.add(boldChunk);
        content.add(new Chunk(" s/o, w/o ",contentFont));
        content.add(new Chunk("Ravi Lodha ", boldFont));

        Paragraph content2 = new Paragraph("has been residing at the following address in Village/Town ", contentFont);
        content2.add(new Chunk("Rawatbhata",boldFont));
        content2.add(new Chunk(" of",contentFont));
        Paragraph content3 = new Paragraph("Kota taluska of Chittor District during the period noted below.", contentFont);
        Paragraph content4 = new Paragraph("Place: Bangalore", contentFont);
        Paragraph content5 = new Paragraph("Date: 31/12/2023", contentFont);

        content4.setAlignment(Element.ALIGN_LEFT);
        content4.setLeading(14f);
        content5.setAlignment(Element.ALIGN_LEFT);
        content5.setLeading(14f);
//        content4.setAlignment(Element.ALIGN_LEFT);
//        content4.setLeading(14f);
//        content5.setAlignment(Element.ALIGN_LEFT);
//        content5.setLeading(14f);
        //been residing at the following address in Village/Town Rawatbhata of Kota taluska of Chittor District during the period noted below.
        try {
            document.add(Chunk.NEWLINE);
            document.add(title);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(content);
            document.add(Chunk.NEWLINE);
            document.add(content2);
            document.add(Chunk.NEWLINE);
            document.add(content3);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(content4);
            document.add(Chunk.NEWLINE);
            document.add(content5);
            }
        catch (Exception e){
            return "not added";
        }
        document.close();
        return "added success";
    }
    @GetMapping("/generate/{userId}")
    public ResponseEntity<Map<String, String>> generateCaptcha(@PathVariable("userId")String userId ) throws IOException {
        String captchaText = generateRandomText();
        userCaptchaMap.put(userId,captchaText);
        storedCaptchaText = captchaText;
        BufferedImage captchaImage = generateCaptchaImage(captchaText);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(captchaImage, "jpg", byteArrayOutputStream);
        String base64Image = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());


        String imageUrl = "data:image/jpeg;base64," + base64Image;


        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);

        return ResponseEntity.ok(response);
    }
    private String generateRandomText() {

        int length = 4;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomText = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(characters.length());
            randomText.append(characters.charAt(index));
        }

        return randomText.toString();
    }
    private BufferedImage generateCaptchaImage(String text) {
        int width = 200;
        int height = 80;




        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);



        g2d.setFont(new java.awt.Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.BLACK);

        // Draw the text on the image
        int x = 40;
        int y = height / 2 + 10;
        g2d.drawString(text, x, y);


        addNoise(g2d, width, height);

        g2d.dispose();

        return image;
    }

    private void addNoise(Graphics2D g2d, int width, int height) {

        int numDots = 5000;
        g2d.setColor(Color.GRAY);

        for (int i = 0; i < numDots; i++) {
            int x = new Random().nextInt(width);
            int y = new Random().nextInt(height);
            g2d.fillOval(x, y, 2, 2);
        }
    }
    @PostMapping("/validate/{userId}")
    public ResponseEntity<String> validateCaptcha(@PathVariable("userId") String userId,@RequestParam String enteredText) {

        String storedCaptchaText = userCaptchaMap.get(userId);
        if (enteredText.equals(storedCaptchaText)) {
            return ResponseEntity.ok("Captcha is valid");
        } else {
            return ResponseEntity.badRequest().body("Captcha is invalid");
        }
    }
//    @GetMapping("/email/{email}")
//    public UserEntity getUserByEmail(@PathVariable("email") String email) {
//        return userRepo.findByEmail(email);
//    }
//    @PostMapping("/getdata")
//    public UserDetails getUserDetails(@RequestBody String email){
//       //UserEntity user = new UserEntity();
//       return this.customUserDetailService.
//
//    }
    @GetMapping("/users/email/{email}")
    public UserEntity getUserByEmail(@PathVariable("email") String email) {
        return this.customUserDetailService.getUserByEmail(email);
    }
    @GetMapping("/signin/{email}/{password}")
    public UserEntity getUserByEmailAndPassword(@PathVariable("email") String email,
                                          @PathVariable("password") String password) {
        return this.customUserDetailService.getUserByEmailAndPassword(email, password);
    }
}