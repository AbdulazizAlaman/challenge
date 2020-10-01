package com.db.awmd.challenge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.TransactionBody;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientAccountBalanceException;
import com.db.awmd.challenge.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transactions")
@Slf4j
public class TransactionController {

	private final TransactionService transactionService;
	
	@Autowired
	  public TransactionController(TransactionService transactionService) {
	    this.transactionService = transactionService;
	  }
	
	  @PostMapping(consumes= MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Object> transferAmount(@RequestBody TransactionBody transactionBody){
		 try {
		  return transactionService.makeTransaction(transactionBody.getFromAccountId(),transactionBody.getToAccountId(),transactionBody.getBalance());
	  } catch (InsufficientAccountBalanceException balance) {
		  log.error(balance.getMessage());
	      return new ResponseEntity<>(balance.getMessage(), HttpStatus.BAD_REQUEST);
	    }catch(Exception e) {
	    	log.error(e.getMessage());
	    }
		return new ResponseEntity<Object>("Encountered Error while processing",HttpStatus.BAD_REQUEST);

	  }
}
