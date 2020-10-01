package com.db.awmd.challenge;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

	private MockMvc mockMvc;

	  @Autowired
	  private TransactionService transactionService;
	  @MockBean
	  private AccountsRepository accountsRepository;
	  @Autowired
	  private WebApplicationContext webApplicationContext;

	  @Before
	  public void prepareMockMvc() {
	    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
	    transactionService.getAccountsRepository().clearAccounts();
	    
	    when(accountsRepository.getAccount("Abc123")).thenReturn(new Account("Abc123"));
		when(accountsRepository.getAccount("Xyz123")).thenReturn(new Account("Xyz123"));
		Account fromAcc = accountsRepository.getAccount("Abc123");
		BigDecimal initalAbcBalance = new BigDecimal("1000.0");
		fromAcc.setBalance(initalAbcBalance);
		Account toAcc = accountsRepository.getAccount("Xyz123");
		BigDecimal initalXyzBalance = new BigDecimal("10.0");
		toAcc.setBalance(initalXyzBalance);
	  }
	  
		/*
		 * Scenario-1: Ideal case. When one account-holder transfers an amount less than
		 * the amount present in his/her account to another holder.
		 */ 
	  @Test
	  public void successCase() throws Exception {
		  this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"fromAccountId\":\"Abc123\",\"toAccountId\":\"Xyz123\",\"balance\":500}")).andExpect(status().isOk());
	  }
	  
	  /*
		 * Scenario-2: Transaction Failed. When one account-holder transfers an amount MORE than
		 * what he/she has in account.
		 */ 
	  @Test
	  public void balanceNotPresent() throws Exception {
		  this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"fromAccountId\":\"Abc123\",\"toAccountId\":\"Xyz123\",\"balance\":5000}")).andExpect(status().isBadRequest());
	  }
	  
	  /*
		 * Scenario-3: Transaction Failed. When any one of the (said) account-holder has no
		 * account. In this case You123 is a unknown user.
		 */
	  @Test
	  public void unknownUserAccount() throws Exception {
		  this.mockMvc.perform(post("/v1/transactions").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"fromAccountId\":\"You123\",\"toAccountId\":\"Xyz123\",\"balance\":5000}")).andExpect(status().isNotFound());
	  }

}
