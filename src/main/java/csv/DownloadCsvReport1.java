package csv;

import com.CourtReserve.app.models.BookSlot;
import com.CourtReserve.app.models.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class DownloadCsvReport1 {

    public static void getCsvReportDownload(HttpServletResponse response, String[] header, List<User> list1, String fileName){

        try{
            System.out.println("in csv report helper class");

            response.setContentType("text/csv");
            String headerKey ="Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", fileName);
            response.setHeader(headerKey,headerValue);


            ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
            csvWriter.writeHeader(header);

            for(User superList : list1){
                csvWriter.write(superList, header);
            }

            csvWriter.close();

        }catch(Exception e){

        }

    }
}
