package com.CourtReserve.app.controllers;

import ch.qos.logback.classic.spi.LoggingEventVO;
import com.CourtReserve.app.models.Referral;
import com.CourtReserve.app.models.User;
import com.CourtReserve.app.models.UserLog;
import com.CourtReserve.app.repositories.ReferralRepository;
import com.CourtReserve.app.repositories.UserLogRepository;
import com.CourtReserve.app.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csv.DownloadCsvReport1;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class entryController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReferralRepository referralRepository;
    @Autowired
    UserLogRepository userLogRepository;
    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;


    @GetMapping("/public/register")
    public String showRegistrationForm(HttpSession session) {

        if (session.getAttribute("verified") != null ){
            if (session.getAttribute("verified").toString().equals( "true")){

                return "entryTemplates/registrationForm";}
        } else {
            return "entryTemplates/verification";
        }
        return "entryTemplates/verification";
    }

    @RequestMapping("/public/login")
    public String showLoginForm(HttpSession session){

        return "entryTemplates/login";
    }
    @PostMapping("/public/login")
    public String loginUser(HttpSession session, @RequestParam Map body, HttpServletResponse response,HttpSession request, Model model) throws UnknownHostException {
        List<String> messages = new ArrayList<>();
        System.out.println(body);
        User user = new User();
        UserLog userLog = new UserLog();
        String result="";
        UserLog usercheck= userLogRepository.findByMobileNoAndStatus(body.get("mobileNo").toString(),"active");
        if(usercheck==null) {
            System.out.println("if exe");
            user = userRepository.findByMobileNoAndPassword(body.get("mobileNo").toString(), body.get("password").toString());
            if (user != null) {
                session.setAttribute("loggedIn", "true");
                session.setAttribute("loggedMobile", user.getMobileNo());
                session.setAttribute("userType", user.getUserType());
                session.setAttribute("user", user.getDict());
                userLog.setUserId(user.getId());
                userLog.setMobileNo((String) body.get("mobileNo"));
                userLog.setIpAddress(String.valueOf(InetAddress.getByName("192.168.29.119")));
                userLog.setSessionId(session.getId());
                userLog.setStatus("active");
                userLogRepository.save(userLog);
                messages.add("Successfully login");
                result = "redirect:/";
            } else {
                messages.add("Pls Enter Valid Credentials");
                result = "entryTemplates/login";
                System.out.println("user invalid credentials");
            }
        }else{
            messages.add("User Already LoggedIn!");
            System.out.println("user already loggedin");
            result= "entryTemplates/login";
        }
        model.addAttribute("messages", messages);
        return result;
    }
    @RequestMapping("/public/logout")
    public String loginUser(HttpSession session){
        List<String> messages = new ArrayList<>();
        UserLog userLog=new UserLog();
        System.out.println(session.getId());
        User user=new User();
        UserLog usercheck= userLogRepository.findBySessionIdAndStatus(session.getId(),"active");
        System.out.println("user Checking:"+usercheck);
        userLogRepository.delete(usercheck);
        session.setAttribute("loggedIn", "false");
        session.setAttribute("loggedMobile", null);
        session.setAttribute("userType", null);
        session.setAttribute("user", null);
        messages.add("Successfully logout");
        return "redirect:/";

    }
    @PostMapping("/public/register")
    public String registerUser(@ModelAttribute User user, HttpSession session,Model model) {
        // Process the user registration
        System.out.println("user:"+user);
        List<String> messages = new ArrayList<>();
        String result="";
        if (user.getReferral().equals("")){
            user.setUserType("NonMember");
        }else{
            System.out.println(referralRepository.findAll());

            Referral referral = referralRepository.findByCode(user.getReferral().toString());

            user.setUserType(referral.getType());
            messages.add(" you Registered Successfully");

        }
        userRepository.save(user);
        session.setAttribute("verified" , null);
        session.setAttribute("mobileNo", null);
        model.addAttribute("messages",messages);

        // Redirect to a success page or perform other actions
        return "redirect:/"; // Name of the success page or URL
    }
    @GetMapping("/public/password")
    public String changePassword(){
        System.out.println("password");
        return "entryTemplates/password";
    }
    @PostMapping("/public/password")
    public String changePassword( HttpServletRequest request, Model model,@RequestParam Map<String, String> body) {
        List<String> messages = new ArrayList<>();
        System.out.println(body);
        String result="";
        User user = userRepository.findByMobileNo(body.get("mobileNo"));
        System.out.println("user:" + user);
        if(user==null){
            messages.add("Pls Enter Registered MobileNo");
            result = "entryTemplates/password";
        }
        else{
            System.out.println("user mob:" + user.getMobileNo());
            System.out.println("user mob:" + body.get("mobileNo"));
            user.setPassword(body.get("newPassword"));
            System.out.println(user);
            messages.add("Your Password Has Been Changed Successfully");
            result ="redirect:entryTemplates/login";
        }
        model.addAttribute("messages",messages);
        return result;
    }


@RequestMapping("/public/pwd")
public String changePwdHtmlForm(Model model, HttpSession session){
    System.out.println("password");
    if (session.getAttribute("loggedIn").equals("true") ){
        UserLog users = userLogRepository.findBySessionIdAndStatus(session.getId(), "active");
        System.out.println(users.getMobileNo());
        User user = (userRepository.findByMobileNo(users.getMobileNo()));
        model.addAttribute("user", user);
        System.out.println(user.getMobileNo());
    return "entryTemplates/changePassword";
}
    List messages = new ArrayList<>();
    messages.add("Login First");
    model.addAttribute("messages", messages);
    return "redirect:/loginPage";

}
    @PostMapping("/public/pwd")
    public String changePwd( HttpServletRequest request, Model model,@RequestParam String mobileNo,  @RequestParam String oldPassword, @RequestParam String newPassword,@RequestParam String con_newpassword,HttpSession session) {
        List<String> messages = new ArrayList<>();

        System.out.println(mobileNo);
        System.out.println(oldPassword);
        System.out.println(newPassword);
        System.out.println(con_newpassword);

        UserLog users = userLogRepository.findBySessionIdAndStatus(session.getId(), "active");
        User user = userRepository.findByMobileNo(users.getMobileNo());
        System.out.println("user:" + user);
        System.out.println("user1:" + users);
        if (user.getMobileNo().equals(mobileNo)) {
            if (user.getPassword().equals(oldPassword)) {
                if (!newPassword.equals(oldPassword)) {
                    if (newPassword.equals(con_newpassword)) {
                        user.setPassword(newPassword);
                        userRepository.save(user);
                        model.addAttribute("messages", "Successfully Changed Ur Password");
                        System.out.println("Successfully Changed Ur Password..");
                    } else {
                        model.addAttribute("messages", "Pls match the New Password with Confirm Password..");
                        System.out.println("Pls match the New Password with Confirm Password..");
                    }

                } else {
                    model.addAttribute("messages", "Pls Re-Enter the New Password to Confirm.");
                    System.out.println("Pls Re-Enter the New Password to Confirm ..");

                }
            } else {
                model.addAttribute("messages", "Pls Enter Valid  Old Password");
                System.out.println("Pls Enter Valid  Old Password..");

            }

        } else {
            model.addAttribute("messages", "Pls Enter Registered MobileNo");
            System.out.println("Pls Enter Registered MobileNo");

        }
        return "redirect:/";
    }
        @RequestMapping("/public/managePro")
    public String changePwdHtmlForm1(Model model, HttpSession session){

        if (session.getAttribute("loggedIn").equals("true") ){
            UserLog userLog = userLogRepository.findBySessionIdAndStatus(session.getId(), "active");
            System.out.println(userLog.getMobileNo());
            String mobNo=userLog.getMobileNo();
            User user1 = userRepository.findByMobileNo(mobNo);
            System.out.println(user1.getMobileNo());
            model.addAttribute("user", user1);
            System.out.println(user1.getMobileNo());
            return "entryTemplates/manageProfile";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";

    }
    @PostMapping("/public/managePro")
    public String manageProUser(@ModelAttribute User user, HttpSession session,Model model) {
        // Process the user registration
        System.out.println("user:"+user);
        List<String> messages = new ArrayList<>();
        User user1=userRepository.findByMobileNo(user.getMobileNo());
        user1.setUserType(user.getUserType());
        user1.setUserName(user.getUserName());
        user1.setEmail(user.getEmail());
        user1.setLocation(user.getLocation());
        user1.setCountry(user.getCountry());
        user1.setUadr1(user.getUadr1());
        user1.setUadr2(user.getUadr2());
        user1.setUadr3(user.getUadr3());
        user1.setUadr4(user.getUadr4());
        user1.setUpincode(user.getUpincode());

        if (user.getReferral().equals("")){
            user.setUserType("NonMember");
        }else{
            System.out.println(referralRepository.findAll());

            Referral referral = referralRepository.findByCode(user1.getReferral().toString());

            user1.setUserType(referral.getType());
            messages.add(" you Manage Profile Successfully");

        }
        userRepository.save(user1);
        session.setAttribute("verified" , null);
        session.setAttribute("mobileNo", null);
        model.addAttribute("messages",messages);

        // Redirect to a success page or perform other actions
        return "redirect:/"; // Name of the success page or URL
    }

    @GetMapping("/userData")
    public String slotViewOrderForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("true")) {
            // List<User> users = (List<User>) userRepository.findAll();
            List<User> users = userRepository.findByOrderByIdDesc();

            System.out.println("users:" + users);
            model.addAttribute("user", users);
            return "admin/userData";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";
    }
    @PostMapping("/userData")
    public @ResponseBody String slotViewOrder(Model model, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        System.out.println("88888888888");
        String userName = request.getParameter("userName");
        System.out.println(userName);
        String userType = request.getParameter("userType");
        System.out.println(userType);
        String country = request.getParameter("country");
        System.out.println(country);
        String referral = request.getParameter("referral");
        System.out.println(referral);
        List<User> list = new ArrayList<>();
        if (!userName.equals("all") && (userType.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list = userRepository.findByUserNameOrderByUserNameAsc(userName);
        } else if (!userType.equals("all") && (userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list = userRepository.findByUserTypeOrderByUserNameAsc(userType);
        } else if (userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list = userRepository.findByCountryOrderByUserNameAsc(country);
        } else if (userType.equals("all") && (userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list = userRepository.findByReferralOrderByUserNameAsc(referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list = userRepository.findByUserNameAndUserTypeOrderByUserNameAsc(userName, userType);
        } else if (userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list = userRepository.findByCountryAndReferralOrderByUserNameAsc(country, referral);
        } else if (userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list = userRepository.findByUserNameAndReferralOrderByUserNameAsc(userName, referral);
        } else if (!userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list = userRepository.findByCountryAndUserTypeOrderByUserNameAsc(country, userType);
        }
        else if (!userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list = userRepository.findByCountryAndUserTypeAndUserNameOrderByUserNameAsc(country, userType, userName);
        } else if (userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list = userRepository.findByCountryAndUserNameAndReferralOrderByUserNameAsc(country, userName, referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list = userRepository.findByUserNameAndUserTypeAndReferralOrderByUserNameAsc(userName, userType, referral);
        } else if (!userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list = userRepository.findByCountryAndUserTypeAndReferralOrderByUserNameAsc(country, userType, referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list = userRepository.findByCountryAndUserTypeAndReferralAndUserNameOrderByUserNameAsc(country, userType, referral, userName);
        } else if (userType.equals("all") && (userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list = (List<User>) userRepository.findByOrderByUserNameAsc();
        }


        ObjectMapper mapper = mapperBuilder.build();
        String output = mapper.writeValueAsString(list);
        //String output1 = mapper.writeValueAsString(list);
        System.out.println("Excel Size -- " + list.size());

        return output;
    }

    @Autowired
    SpringTemplateEngine springTemplateEngine;

    @GetMapping("/userPdfData")
    public ResponseEntity slotViewOrder1(HttpSession session, @RequestParam Map<String, String> body, Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
        System.out.println("88888888888");
        String userName = request.getParameter("userName");
        System.out.println(userName);
        String userType = request.getParameter("userType");
        System.out.println(userType);
        String country = request.getParameter("country");
        System.out.println(country);
        String referral = request.getParameter("referral");
        System.out.println(referral);
        List<User> list1 = new ArrayList<>();
        if (!userName.equals("all") && (userType.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByUserNameOrderByUserNameAsc(userName);
        } else if (!userType.equals("all") && (userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByUserTypeOrderByUserNameAsc(userType);
        } else if (userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByCountryOrderByUserNameAsc(country);
        } else if (userType.equals("all") && (userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByReferralOrderByUserNameAsc(referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByUserNameAndUserTypeOrderByUserNameAsc(userName, userType);
        } else if (userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndReferralOrderByUserNameAsc(country, referral);
        } else if (userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByUserNameAndReferralOrderByUserNameAsc(userName, referral);
        } else if (!userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeOrderByUserNameAsc(country, userType);
        }
        else if (!userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeAndUserNameOrderByUserNameAsc(country, userType, userName);
        } else if (userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserNameAndReferralOrderByUserNameAsc(country, userName, referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByUserNameAndUserTypeAndReferralOrderByUserNameAsc(userName, userType, referral);
        } else if (!userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeAndReferralOrderByUserNameAsc(country, userType, referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeAndReferralAndUserNameOrderByUserNameAsc(country, userType, referral, userName);
        } else if (userType.equals("all") && (userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = (List<User>) userRepository.findByOrderByUserNameAsc();
        }
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("list",list1);
        String finalhtml = springTemplateEngine.process("admin/userPdfData",context);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        System.out.println(finalhtml);
        renderer.setDocumentFromString(finalhtml);
        renderer.layout();
        renderer.createPDF(ops, false);
        renderer.finishPDF();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + userName.toString()  + ".pdf").contentType(MediaType.APPLICATION_OCTET_STREAM).body(ops.toByteArray());

    }

    @GetMapping("/userExcelData")
    public ResponseEntity slotViewOrder2(HttpSession session, @RequestParam Map<String, String> body, Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
        System.out.println("88888888888");
        String userName = request.getParameter("userName");
        System.out.println(userName);
        String userType = request.getParameter("userType");
        System.out.println(userType);
        String country = request.getParameter("country");
        System.out.println(country);
        String referral = request.getParameter("referral");
        System.out.println(referral);
        List<User> list1 = new ArrayList<>();
        if (!userName.equals("all") && (userType.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByUserNameOrderByUserNameAsc(userName);
        } else if (!userType.equals("all") && (userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByUserTypeOrderByUserNameAsc(userType);
        } else if (userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByCountryOrderByUserNameAsc(country);
        } else if (userType.equals("all") && (userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByReferralOrderByUserNameAsc(referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByUserNameAndUserTypeOrderByUserNameAsc(userName, userType);
        } else if (userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndReferralOrderByUserNameAsc(country, referral);
        } else if (userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByUserNameAndReferralOrderByUserNameAsc(userName, referral);
        } else if (!userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeOrderByUserNameAsc(country, userType);
        }
        else if (!userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeAndUserNameOrderByUserNameAsc(country, userType, userName);
        } else if (userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserNameAndReferralOrderByUserNameAsc(country, userName, referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByUserNameAndUserTypeAndReferralOrderByUserNameAsc(userName, userType, referral);
        } else if (!userType.equals("all") && (userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeAndReferralOrderByUserNameAsc(country, userType, referral);
        } else if (!userType.equals("all") && (!userName.equals("all") && (!country.equals("all") && (!referral.equals("all"))))) {
            list1 = userRepository.findByCountryAndUserTypeAndReferralAndUserNameOrderByUserNameAsc(country, userType, referral, userName);
        } else if (userType.equals("all") && (userName.equals("all") && (country.equals("all") && (referral.equals("all"))))) {
            list1 = (List<User>) userRepository.findByOrderByUserNameAsc();
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(body.get("userName").toString());
        HSSFRow Header = sheet.createRow(0);
        int headercellStart = 0;
        String header[] = {"userName", "userType", "MobileNo", "email", "uadr1", "uadr2", "uadr3", "location", "country", "upincode", "referral", "lastLogin"};
        DownloadCsvReport1.getCsvReportDownload(response, header, list1, "name.csv");
        return (ResponseEntity) ResponseEntity.status(203);
    }


    @GetMapping("/userReports")
    public String slotViewOrderForm5(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("true")) {
            UserLog userLog = userLogRepository.findBySessionIdAndStatus(session.getId(), "active");
            System.out.println(userLog.getMobileNo());
            String mobNo=userLog.getMobileNo();
            User user1 = userRepository.findByMobileNo(mobNo);
            System.out.println(user1.getMobileNo());
            model.addAttribute("user", user1);

            return "customer/userReports";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";
    }
//    @PostMapping("/userReports")
//    public @ResponseBody String slotViewOrder1(Model model, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
//        System.out.println("88888888888");
//        String mobileNo = request.getParameter("mobileNo");
//        System.out.println(mobileNo);
//        User user10 =  userRepository.findByMobileNo(mobileNo);
//        System.out.println("Raam:" + user10);
//        model.addAttribute("user",user10);
//        ObjectMapper mapper = mapperBuilder.build();
//        String output = mapper.writeValueAsString(user10);
//        return output;
//    }

}
