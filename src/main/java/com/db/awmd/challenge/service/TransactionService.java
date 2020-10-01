package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InsufficientAccountBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.Setter;

@Service
public class TransactionService {

	@Getter @Setter
	private AccountsRepository accountsRepository;
	@Getter @Setter
	private NotificationService notificationService;
	
	@Value("${debitedString}")
	private String debitedString;
	@Value("${creditedString}")
	private String creditedString;
	
	@Autowired
	  public TransactionService(AccountsRepository accountsRepository,NotificationService notificationService) {
		this.accountsRepository=accountsRepository;
		this.notificationService=notificationService;
	}
	
	protected static final class TransactionUnit{
		protected static int noOfTransactions=0;
		
		protected static int getNoOfTransactions() {
			return noOfTransactions;
		}

		public void increaseTransactionUnit() {
			noOfTransactions++;
		}
		
	}
	
	final static TransactionUnit[] list = new TransactionUnit[1000];
	static {
		for(int i=0;i<1000;i++) {
			list[i]=new TransactionUnit();
		}
	}
	
	public ResponseEntity<Object> makeTransaction(String fromAcc, String toAcc, BigDecimal amt) throws InsufficientAccountBalanceException{
		ResponseEntity<Object> response = null;
		if(TransactionUnit.getNoOfTransactions()<1000) {
			Account fromAccount = accountsRepository.getAccount(fromAcc);
			Account toAccount = accountsRepository.getAccount(toAcc);
			if(fromAccount!=null && toAccount!=null) {
				TransactionUnit unit = list[code(fromAcc)];
				synchronized(unit) {
					unit.increaseTransactionUnit();
					BigDecimal fromBalance = fromAccount.getBalance();
					if(fromBalance.compareTo(amt)<0)
						throw new InsufficientAccountBalanceException("Insufficient Balance. Transaction Failed!");
					else {
						fromAccount.setBalance(fromBalance.subtract(amt));
						toAccount.setBalance(toAccount.getBalance().add(amt));
						String fromMsg = String.format(debitedString, String.valueOf(amt), toAccount.getAccountId().toString(), String.valueOf(fromAccount.getBalance()));
						String toMsg = String.format(creditedString,String.valueOf(amt), fromAccount.getAccountId().toString(), String.valueOf(toAccount.getBalance()));
						notificationService.notifyAboutTransfer(toAccount,toMsg);
						notificationService.notifyAboutTransfer(toAccount, fromMsg);
						response=new ResponseEntity<>("Transaction Successful!",HttpStatus.OK);
					}
				}
			}else {
				response=new ResponseEntity<>("Account do not exist.",HttpStatus.NOT_FOUND);
			}
		}
		return response;
	}

	private int code(String fromAcc) {
		int code = 0;
		code = fromAcc.hashCode();
		code = Math.abs(code);
		while(code>1000) {
			code = code % 1000;
		}
		return (int)code;
	}
}
