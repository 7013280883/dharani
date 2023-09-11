package com.CourtReserve.app.controllers;

import com.CourtReserve.app.models.BookSlot;
import com.CourtReserve.app.models.Game;
import com.CourtReserve.app.repositories.BookSlotRepository;
import com.CourtReserve.app.repositories.GameRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csv.DownloadCsvReport;
import csv.DownloadCsvReport4;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class GameController {
    private final GameRepository gamesRepository;
    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;
    @Autowired
    private BookSlotRepository bookSlotRepository;

    public GameController(GameRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    @RequestMapping("/games")
    public String showGames(Model model){
        Iterable<Game> games = gamesRepository.findAll();
        model.addAttribute("games", games);

        return "masters/game";
    }
    @PostMapping("/games")
    public String addGames(@ModelAttribute Game game, Model model){
        gamesRepository.save(game);

        return "redirect:/games";
    }

    @GetMapping("/gameDataUser")
    public String gameDataUserForm(Model model, HttpSession session){
        if (session.getAttribute("loggedIn").equals("true") ){
            Iterable<Game> g =  gamesRepository.findAll();
            model.addAttribute("game", g);
            return "admin/game";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";

    }
    @PostMapping("/gameDataUser")
    public @ResponseBody String gameViewDataUser(HttpServletResponse response , HttpServletRequest request) throws JsonProcessingException {
        System.out.println("88888888888");
        String code=request.getParameter("code");
        String name=request.getParameter("name");
        System.out.println(name);
        String gameMode=request.getParameter("gameMode");
        System.out.println(gameMode);
        String status=request.getParameter("status");
        System.out.println(status);
        String fromDate= String.valueOf(LocalDate.parse(request.getParameter("fromDate")));
        System.out.println(fromDate);
        LocalDate toDate= LocalDate.parse(request.getParameter("toDate"));
        System.out.println(toDate);
        List<BookSlot> list = bookSlotRepository.findByGameDateBetweenOrderByGameNameAsc(LocalDate.parse(fromDate),toDate);
        System.out.println(list);
        System.out.println("game:"+list.size());

        if(name.equals("all")&&!status.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameModeOrderByGameNameAsc(LocalDate.parse(fromDate),toDate,status,gameMode);
        }
        else if(!status.equals("all") && !name.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameNameAndGameModeOrderByGameNameAsc(LocalDate.parse(fromDate),toDate,status,name,gameMode);
        }
        else if(!status.equals("all") && name.equals("all") && gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusOrderByGameNameAsc(LocalDate.parse(fromDate),toDate,status);
        }
        else if(name.equals("all") && status.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndGameModeOrderByGameNameAsc(LocalDate.parse(fromDate),toDate,gameMode);
        }
        else if(!name.equals("all") && (status.equals("all") && !gameMode.equals("all"))){
            list = bookSlotRepository.findByGameDateBetweenAndGameNameAndGameModeOrderByGameNameAsc(LocalDate.parse(fromDate),toDate,name,gameMode);
        }

        else if(!status.equals("all") && !name.equals("all") && gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameNameOrderByGameNameAsc(LocalDate.parse(fromDate),toDate,status,name);
        }
        else if(status.equals("all") && !name.equals("all") && gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndGameNameOrderByGameNameAsc(LocalDate.parse(fromDate),toDate,name);
        }





        ObjectMapper mapper = mapperBuilder.build();
        String  output = mapper.writeValueAsString(list);

        System.out.println("Excel Size -- " + list.size());
        //  model.addAttribute("list", list);
        return output;


    }
    @Autowired
    SpringTemplateEngine springTemplateEngine;
    @GetMapping("/gamePdfDataMember")
    public ResponseEntity slotViewPdfOrder(Model model, HttpServletResponse response, HttpServletRequest request) {
        String code=request.getParameter("code");
        String name=request.getParameter("name");
        String gameMode=request.getParameter("gameMode");
        String status=request.getParameter("status");
        LocalDate fromDate= LocalDate.parse(request.getParameter("fromDate"));
        LocalDate toDate= LocalDate.parse(request.getParameter("toDate"));
        List<BookSlot> list = bookSlotRepository.findByGameDateBetweenOrderByGameNameAsc(fromDate,toDate);
        if(name.equals("all")&&!status.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameModeOrderByGameNameAsc(fromDate,toDate,status,gameMode);
        }
        else if(!status.equals("all") && !name.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameNameAndGameModeOrderByGameNameAsc(fromDate,toDate,status,name,gameMode);
        }
        else if(!status.equals("all") && name.equals("all") && gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusOrderByGameNameAsc(fromDate,toDate,status);
        }
        else if(name.equals("all") && status.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndGameModeOrderByGameNameAsc(fromDate,toDate,gameMode);
        }
        else if(!name.equals("all") && (status.equals("all") && !gameMode.equals("all"))){
            list = bookSlotRepository.findByGameDateBetweenAndGameNameAndGameModeOrderByGameNameAsc(fromDate,toDate,name,gameMode);
        }

        else if(!status.equals("all") && !name.equals("all") && gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameNameOrderByGameNameAsc(fromDate,toDate,status,name);
        }

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("list", list);
        if(list.isEmpty()){
            System.out.println("stophere");
            //response1.putIfAbsent("msg","No Records Found");
            //response1.put("status", 202);
        }else {
            String finalhtml = springTemplateEngine.process("admin/gamePdfData", context);
            ByteArrayOutputStream ops = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            System.out.println(finalhtml);
            renderer.setDocumentFromString(finalhtml);
            renderer.layout();
            renderer.createPDF(ops, false);
            renderer.finishPDF();
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name + ".pdf").contentType(MediaType.APPLICATION_OCTET_STREAM).body(ops.toByteArray());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/gameDataUser");

        return new ResponseEntity("No Records Found",headers, HttpStatus.FOUND);
    }
    @GetMapping("/gameExcelDataMember")
    public ResponseEntity slotViewOrder1(HttpSession session, @RequestParam Map<String, String> body, Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {

        String code=request.getParameter("code");
        String name=request.getParameter("name");
        String gameMode=request.getParameter("gameMode");
        String status=request.getParameter("status");
        LocalDate fromDate= LocalDate.parse(request.getParameter("fromDate"));
        LocalDate toDate= LocalDate.parse(request.getParameter("toDate"));
        List<BookSlot> list = bookSlotRepository.findByGameDateBetweenOrderByGameNameAsc(fromDate,toDate);
        if(name.equals("all")&&!status.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameModeOrderByGameNameAsc(fromDate,toDate,status,gameMode);
        }
        else if(!status.equals("all") && !name.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameNameAndGameModeOrderByGameNameAsc(fromDate,toDate,status,name,gameMode);
        }
        else if(!status.equals("all") && name.equals("all") && gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusOrderByGameNameAsc(fromDate,toDate,status);
        }
        else if(name.equals("all") && status.equals("all") && !gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndGameModeOrderByGameNameAsc(fromDate,toDate,gameMode);
        }
        else if(!name.equals("all") && (status.equals("all") && !gameMode.equals("all"))){
            list = bookSlotRepository.findByGameDateBetweenAndGameNameAndGameModeOrderByGameNameAsc(fromDate,toDate,name,gameMode);
        }

        else if(!status.equals("all") && !name.equals("all") && gameMode.equals("all")){
            list = bookSlotRepository.findByGameDateBetweenAndConfirmStatusAndGameNameOrderByGameNameAsc(fromDate,toDate,status,name);
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(body.get("name").toString());
        HSSFRow Header = sheet.createRow(0);
        int headercellStart = 0;
        String header[] ={"gameDate","gameName","courtCode","startTime","endTime","slotCode","gameMode","confirmStatus","bookedBy","bookTime","approvedBy","RemarksByUser","RemarksByAdmin"};
        if(list.isEmpty()){
            System.out.println("stophere");
            //response1.putIfAbsent("msg","No Records Found");
            //response1.put("status", 202);
        }else {
            DownloadCsvReport.getCsvReportDownload(response, header, list, "code.csv");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/gameDataUser");

        return new ResponseEntity<byte []>(null,headers,HttpStatus.FOUND);


    }


    @GetMapping("/gameData")
    public String slotViewOrderForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("true")) {
            // List<User> users = (List<User>) userRepository.findAll();
            List<Game> games = gamesRepository.findByOrderByIdDesc();
            System.out.println("games:" + games);
            model.addAttribute("game", games);
            return "admin/gameData";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";
    }
    @PostMapping("/gameData")
    public @ResponseBody String slotViewOrder(Model model, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        System.out.println("88888888888");
        String name = request.getParameter("name");
        System.out.println(name);
//        String code = request.getParameter("code");
//        System.out.println(code);
//
        List list = (List) gamesRepository.findByOrderByNameAsc();
//        if(!name.equals("all")&&(code.equals("all"))){
//            list= gamesRepository.findByName(name);
//            System.out.println("kalyan1:"+list);
//        }
//        else if(name.equals("all")&&(!code.equals("all"))){
//            list= gamesRepository.findByCode(code);
//            System.out.println("kalyan2:"+list);
//        }
//        else if(!name.equals("all")&&(!code.equals("all"))){
//            list= gamesRepository.findByNameAndCode(name,code);
//            System.out.println("kalyan3:"+list);
//        }

        ObjectMapper mapper = mapperBuilder.build();
        String output = mapper.writeValueAsString(list);
        System.out.println("Excel Size -- " + list.size());

        return output;
    }
    @GetMapping("/gamePdfData")
    public ResponseEntity slotViewPdfUserOrder1(Model model, HttpServletResponse response, HttpServletRequest request) {
        System.out.println("88888888888");
        String name = request.getParameter("name");
        System.out.println(name);
        List list = (List) gamesRepository.findByOrderByNameAsc();
        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("list", list);
        String finalhtml = springTemplateEngine.process("admin/gamePdfData",context);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        System.out.println(finalhtml);
        renderer.setDocumentFromString(finalhtml);
        renderer.layout();
        renderer.createPDF(ops, false);
        renderer.finishPDF();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+ request.getSession().getAttribute("loggedMobile").toString()+".pdf").contentType(MediaType.APPLICATION_OCTET_STREAM).body(ops.toByteArray());

    }
    @GetMapping("/gameExcelData")
    public ResponseEntity slotViewUserOrder2(HttpSession session, @RequestParam Map<String, String> body, Model model, HttpServletResponse response, HttpServletRequest request) throws Exception {
        System.out.println("88888888888");
        String name = request.getParameter("name");
        System.out.println(name);
        List list1 = (List) gamesRepository.findByOrderByNameAsc();
        System.out.println("list:"+list1);
        System.out.println("Excel Size -- " + list1.size());
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(request.getSession().getAttribute("loggedMobile").toString());
        HSSFRow Header = sheet.createRow(0);
        int headercellStart = 0;
        String header[] ={"code","name"};
        // String header[]={"gameDate","slotCode","game"};
        DownloadCsvReport4.getCsvReportDownload(response, header, list1,"name.csv");

        return (ResponseEntity) ResponseEntity.status(203);

    }




}
