package com.CourtReserve.app.controllers;

import com.CourtReserve.app.models.DayType;
import com.CourtReserve.app.models.Slot;
import com.CourtReserve.app.models.SpecialDates;
import com.CourtReserve.app.repositories.CourtRepository;
import com.CourtReserve.app.repositories.DayTypeRepository;
import com.CourtReserve.app.repositories.SpecialDatesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csv.DownloadCsvReport2;
import csv.DownloadCsvReport3;
import csv.DownloadCsvReport5;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class specialDatesController {

    @Autowired
    private SpecialDatesRepository specialDatesRepository;
    @Autowired
    private DayTypeRepository dayTypeRepository;
    @Autowired
    private CourtRepository courtRepository;
    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;

    @RequestMapping("/specialDates")
    public String showSpecialDates(Model model){
        Iterable<SpecialDates> specialDates = specialDatesRepository.findAll();
        model.addAttribute("courts", courtRepository.findAll());
        model.addAttribute("specialDates", specialDates);
        model.addAttribute("dayTypes", dayTypeRepository.findAll());

        return "masters/specialDates";
    }
    @PostMapping("/specialDates")
    public String addSpecialDate(@ModelAttribute SpecialDates specialDates, Model model){
        specialDatesRepository.save(specialDates);

        return "redirect:/specialDates";
    }
    @GetMapping("/specialDatesData")
    public String slotViewOrderForm1(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("true")) {
            // List<User> users = (List<User>) userRepository.findAll();
            List<SpecialDates> specialDates = specialDatesRepository.findByOrderByIdDesc();
            System.out.println("specialDates:" + specialDates);
            model.addAttribute("specialDate", specialDates);
            return "admin/specialDatesData";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";
    }
    @PostMapping("/specialDatesData")
    public @ResponseBody String slotViewOrder1(Model model, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        System.out.println("88888888888");
        String courtCode = request.getParameter("courtCode");
        System.out.println(courtCode);
        String dayType = request.getParameter("dayType");
        System.out.println(dayType);
        List<SpecialDates> list = new ArrayList<>();
        if(courtCode.equals("all")&&(dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByOrderByCourtCodeAsc();
            System.out.println("kalyan:"+list);
        }
        else if(!courtCode.equals("all")&&(dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByCourtCodeOrderByCourtCodeAsc(courtCode);
            System.out.println("kalyan1:"+list);
        }
        else if(courtCode.equals("all")&&(!dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByDayTypeOrderByCourtCodeAsc(dayType);
            System.out.println("kalyan2:"+list);
        }
        else if(!courtCode.equals("all")&&(!dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByCourtCodeAndDayTypeOrderByCourtCodeAsc(courtCode,dayType);
            System.out.println("kalyan2:"+list);
        }

        ObjectMapper mapper = mapperBuilder.build();
        String output = mapper.writeValueAsString(list);
        //String output1 = mapper.writeValueAsString(list);
        System.out.println("Excel Size -- " + list.size());

        return output;
    }
    @Autowired
    SpringTemplateEngine springTemplateEngine;
    @GetMapping("/specialDatesPdfData")
    public ResponseEntity slotViewPdfUserOrder1(Model model, HttpServletResponse response, HttpServletRequest request) {
        System.out.println("88888888888");
        String courtCode = request.getParameter("courtCode");
        System.out.println(courtCode);
        String dayType = request.getParameter("dayType");
        System.out.println(dayType);
        List<SpecialDates> list = new ArrayList<>();
        if(courtCode.equals("all")&&(dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByOrderByCourtCodeAsc();
            System.out.println("kalyan:"+list);
        }
        else if(!courtCode.equals("all")&&(dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByCourtCodeOrderByCourtCodeAsc(courtCode);
            System.out.println("kalyan1:"+list);
        }
        else if(courtCode.equals("all")&&(!dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByDayTypeOrderByCourtCodeAsc(dayType);
            System.out.println("kalyan2:"+list);
        }
        else if(!courtCode.equals("all")&&(!dayType.equals("all"))){
            list = (List<SpecialDates>) specialDatesRepository.findByCourtCodeAndDayTypeOrderByCourtCodeAsc(courtCode,dayType);
            System.out.println("kalyan2:"+list);
        }

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("list", list);
        String finalhtml = springTemplateEngine.process("admin/specialDatesPdfData",context);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        System.out.println(finalhtml);
        renderer.setDocumentFromString(finalhtml);
        renderer.layout();
        renderer.createPDF(ops, false);
        renderer.finishPDF();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+ request.getSession().getAttribute("loggedMobile").toString()+".pdf").contentType(MediaType.APPLICATION_OCTET_STREAM).body(ops.toByteArray());

    }
    @GetMapping("/specialDatesExcelData")
    public ResponseEntity slotViewUserOrder2(HttpSession session, @RequestParam Map<String, String> body, Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
        System.out.println("88888888888");
        String courtCode = request.getParameter("courtCode");
        System.out.println(courtCode);
        String dayType = request.getParameter("dayType");
        System.out.println(dayType);
        List<SpecialDates> list1 = new ArrayList<>();
        if(courtCode.equals("all")&&(dayType.equals("all"))){
            list1 = (List<SpecialDates>) specialDatesRepository.findByOrderByCourtCodeAsc();
            System.out.println("kalyan:"+list1);
        }
        else if(!courtCode.equals("all")&&(dayType.equals("all"))){
            list1 = (List<SpecialDates>) specialDatesRepository.findByCourtCodeOrderByCourtCodeAsc(courtCode);
            System.out.println("kalyan1:"+list1);
        }
        else if(courtCode.equals("all")&&(!dayType.equals("all"))){
            list1 = (List<SpecialDates>) specialDatesRepository.findByDayTypeOrderByCourtCodeAsc(dayType);
            System.out.println("kalyan2:"+list1);
        }
        else if(!courtCode.equals("all")&&(!dayType.equals("all"))){
            list1 = (List<SpecialDates>) specialDatesRepository.findByCourtCodeAndDayTypeOrderByCourtCodeAsc(courtCode,dayType);
            System.out.println("kalyan2:"+list1);
        }
        System.out.println("list:"+list1);
        System.out.println("Excel Size -- " + list1.size());
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(request.getSession().getAttribute("loggedMobile").toString());
        HSSFRow Header = sheet.createRow(0);
        int headercellStart = 0;
        String header[] ={"date","courtCode","dayType"};
        // String header[]={"gameDate","slotCode","game"};
        DownloadCsvReport5.getCsvReportDownload(response, header, list1, "courtCode.csv");

        return (ResponseEntity) ResponseEntity.status(203);

    }
}
