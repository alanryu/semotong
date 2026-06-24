package kr.co.ucomp.common.exception.handler;



import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;



@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        
        System.out.println(e);
        return "pages/cmm/error/500"; // cmm/500.html로 포워딩
    }
	
	@ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFoundException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "pages/cmm/error/404"; // cmm/404.html로 포워딩
    }
	
	
	@ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "pages/cmm/error/900"; // cmm/900.html로 포워딩
    }
	
}

