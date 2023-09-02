package step.learning.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileWriterFilter implements Filter {
    private final String fileName = "urlLogFile.txt";
    private  FilterConfig _filterConfig;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        _filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;

        String dirPath = _filterConfig.getServletContext().getRealPath("./");
        String foolPath = dirPath + fileName;


        String strToWrite = String.format("%s : %s : %s",
                                                       new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                                                       httpServletRequest.getMethod(),
                                                       httpServletRequest.getRequestURI() );

        File file = new File(foolPath);
        if(!file.exists()){
           try(BufferedWriter bw = new BufferedWriter( new FileWriter(foolPath,false))){
                bw.write(strToWrite);
                bw.newLine();
           }catch (IOException ex){
               System.out.println(ex.getMessage());
           }
        }else{
            try(BufferedWriter bw = new BufferedWriter( new FileWriter(foolPath,true))){
                bw.write(strToWrite);
                bw.newLine();
            }catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        _filterConfig = null;
    }
}
