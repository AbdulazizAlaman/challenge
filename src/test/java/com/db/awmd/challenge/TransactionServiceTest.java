package com.db.awmd.challenge;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InsufficientAccountBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

	@Autowired
	private TransactionService transactionService;
	@MockBean
	private AccountsRepository accountsRepository;
	@Autowired
	private NotificationService notificationService;
	
	@Before
	public void init() {
		transactionService.setAccountsRepository(accountsRepository);
		transactionService.setNotificationService(notificationService);
	}
	
	@Test
	public void makeTransactionTest() throws InsufficientAccountBalanceException {
		when(accountsRepository.getAccount("Abc123")).thenReturn(new Account("Abc123"));
		when(accountsRepository.getAccount("Xyz123")).thenReturn(new Account("Xyz123"));
		
		Account fromAcc = accountsRepository.getAccount("Abc123");
		BigDecimal initalAbcBalance = new BigDecimal("1000.0");
		fromAcc.setBalance(initalAbcBalance);
		Account toAcc = accountsRepository.getAccount("Xyz123");
		BigDecimal initalXyzBalance = new BigDecimal("10.0");
		toAcc.setBalance(initalXyzBalance);
		BigDecimal amt =new BigDecimal("100");
		assertEquals(transactionService.makeTransaction(fromAcc.getAccountId(), toAcc.getAccountId(), amt).getBody(),"Transaction Successful!");
		assertEquals(initalAbcBalance.subtract(amt).longValue(), fromAcc.getBalance().longValue());
		assertEquals(initalXyzBalance.add(amt).longValue(),toAcc.getBalance().longValue());
		amt =new BigDecimal("1000");
		try {
		transactionService.makeTransaction(toAcc.getAccountId(), fromAcc.getAccountId(),amt);
		}catch(InsufficientAccountBalanceException e){
			assertEquals(e.getMessage(),"Insufficient Balance. Transaction Failed!");
		}
		assertEquals(transactionService.makeTransaction(null, fromAcc.getAccountId(),amt).getStatusCode(),HttpStatus.NOT_FOUND);
	}
	
}
