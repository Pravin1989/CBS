package com.imagination.cbs.security;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imagination.cbs.dto.ErrorResponse;
import com.imagination.cbs.exception.CBSAuthenticationException;
@Component(value="googleIDTokenValidationFilter")
public class GoogleIDTokenValidationFilter extends OncePerRequestFilter {

	@Autowired
	private GoogleIDTokenValidationUtility googleIDTokenValidationUtility;

	private static Logger logger = LoggerFactory.getLogger(GoogleIDTokenValidationFilter.class);


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logger.info("inside filter");
		
	
			try {
				if (googleIDTokenValidationUtility.validateAccessToken(request)) {
					
					chain.doFilter(request, response);
					
				}else{
					logger.info("Id token is invalid");
					
					throw new CBSAuthenticationException("ID token is not valid");
				}
			} catch (CBSAuthenticationException e) {
				
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setMessage(e.getErrorMessage());
				errorResponse.setErrorCode(HttpStatus.UNAUTHORIZED.value());
	            
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
	           
	            response.getWriter().write(convertObjectToJson(errorResponse));
	            
	            logger.error(e.getErrorMessage());
	            
			}catch(GeneralSecurityException e){
				e.printStackTrace();
			}
		

	}
	
	public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}