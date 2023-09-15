package com.CourtReserve.app.controllers;

import com.CourtReserve.app.models.Court;
import com.CourtReserve.app.models.Slot;
import com.CourtReserve.app.models.User;
import com.CourtReserve.app.repositories.CourtRepository;
import com.CourtReserve.app.repositories.GameRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csv.DownloadCsvReport2;
import csv.DownloadCsvReport3;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class courtController {
    @Autowired
    CourtRepository courtRepository;
    private final GameRepository gameRepository;
    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;

    public courtController(CourtRepository courtRepository,
                           GameRepository gameRepository) {
        this.courtRepository = courtRepository;
        this.gameRepository = gameRepository;
    }

    @RequestMapping("/courts")
    public String showAddCourt(Model model) {
        Iterable<Court> courts = courtRepository.findAll();
        model.addAttribute("courts", courts);
        model.addAttribute("games", gameRepository.findAll());

        return "masters/court";
    }

    @PostMapping("/courts")
    public String addCourt(@ModelAttribute Court court, Model model, HttpServletRequest request) {
        String startdate = request.getParameter("fromDate");
        String enddate = request.getParameter("toDate");
        System.out.println(startdate);
        System.out.println(enddate);
        System.out.println(court);
        List<Court> courts = courtRepository.findByName(court.getName().toString());
        String gameCode = court.getName().split("-")[0];
        String code = gameCode + "-" + 1;
        if (!courts.isEmpty()) {
            Court courtLatest = courts.get(courts.size() - 1);
            code = gameCode + "-" + (Integer.parseInt(courtLatest.getCode().split("-")[1]) + 1);
        }
        court.setCode(code);
        court.setName(court.getName().toString().split("-")[1]);
        court.setStartDate(startdate);
        court.setEndDate(enddate);
        courtRepository.save(court);

        return "redirect:/courts";
    }

    @GetMapping("/courtData")
    public String slotViewOrderForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("true")) {
            // List<User> users = (List<User>) userRepository.findAll();
            List<Court> courts = courtRepository.findByOrderByIdDesc();
            System.out.println("courts:" + courts);
            System.out.println("courts:" + courts.size());
            Set<String> uniqueUsernames = new HashSet<>();
            List<Court> filteredUsers = courts.stream()
                    .filter(user -> uniqueUsernames.add(user.getAuthority()))
                    .collect(Collectors.toList());
            System.out.println("users1:" + filteredUsers);
            System.out.println("users2:" + filteredUsers.size());
            model.addAttribute("court", courts);
            model.addAttribute("courts", filteredUsers);
            return "admin/courtData";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";
    }

    @PostMapping("/courtData")
    public @ResponseBody String slotViewOrder(Model model, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        System.out.println("88888888888");
        String name = request.getParameter("name");
        System.out.println(name);
        String authority = request.getParameter("authority");
        System.out.println(authority);
//
        List list = (List) courtRepository.findByOrderByCodeAsc();
        if(!name.equals("all")&&(authority.equals("all"))){
            list= courtRepository.findByName(name);
            System.out.println("kalyan:"+list);
        }
        else if(name.equals("all")&&(!authority.equals("all"))){
            list= courtRepository.findByAuthority(authority);
            System.out.println("kalyan:"+list);
        }
        else if(!name.equals("all")&&(!authority.equals("all"))){
            list= courtRepository.findByNameAndAuthority(name,authority);
            System.out.println("kalyan:"+list);
        }

        ObjectMapper mapper = mapperBuilder.build();
        String output = mapper.writeValueAsString(list);
        System.out.println("Excel Size -- " + list.size());

        return output;
    }
    @Autowired
    SpringTemplateEngine springTemplateEngine;
    @GetMapping("/courtPdfData")
    public ResponseEntity slotViewPdfUserOrder1(Model model, HttpServletResponse response, HttpServletRequest request) {
        System.out.println("88888888888");
        String name = request.getParameter("name");
        System.out.println(name);
        String authority = request.getParameter("authority");
        System.out.println(authority);
//
        List list = (List) courtRepository.findByOrderByCodeAsc();
        if(!name.equals("all")&&(authority.equals("all"))){
            list= courtRepository.findByName(name);
            System.out.println("kalyan:"+list);
        }
        else if(name.equals("all")&&(!authority.equals("all"))){
            list= courtRepository.findByAuthority(authority);
            System.out.println("kalyan:"+list);
        }
        else if(!name.equals("all")&&(!authority.equals("all"))){
            list= courtRepository.findByNameAndAuthority(name,authority);
            System.out.println("kalyan:"+list);
        }


        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("list", list);
        String finalhtml = springTemplateEngine.process("admin/courtPdfData",context);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        System.out.println(finalhtml);
        renderer.setDocumentFromString(finalhtml);
        renderer.layout();
        renderer.createPDF(ops, false);
        renderer.finishPDF();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+ request.getSession().getAttribute("loggedMobile").toString()+".pdf").contentType(MediaType.APPLICATION_OCTET_STREAM).body(ops.toByteArray());

    }
    @GetMapping("/courtExcelData")
    public ResponseEntity slotViewUserOrder2(HttpSession session, @RequestParam Map<String, String> body, Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
        System.out.println("88888888888");
        String name = request.getParameter("name");
        System.out.println(name);
        String authority = request.getParameter("authority");
        System.out.println(authority);
//
        List list1 = (List) courtRepository.findByOrderByCodeAsc();
        if(!name.equals("all")&&(authority.equals("all"))){
            list1= courtRepository.findByName(name);
            System.out.println("kalyan:"+list1);
        }
        else if(name.equals("all")&&(!authority.equals("all"))){
            list1= courtRepository.findByAuthority(authority);
            System.out.println("kalyan:"+list1);
        }
        else if(!name.equals("all")&&(!authority.equals("all"))){
            list1= courtRepository.findByNameAndAuthority(name,authority);
            System.out.println("kalyan:"+list1);
        }

        System.out.println("list:"+list1);
        System.out.println("Excel Size -- " + list1.size());
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(request.getSession().getAttribute("loggedMobile").toString());
        HSSFRow Header = sheet.createRow(0);
        int headercellStart = 0;
        String header[] ={"code","name","authority","startDate","endDate"};
        // String header[]={"gameDate","slotCode","game"};
        DownloadCsvReport3.getCsvReportDownload(response, header, list1, "name.csv");

        return (ResponseEntity) ResponseEntity.status(203);

    }
}