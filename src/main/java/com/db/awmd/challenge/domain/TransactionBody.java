package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TransactionBody {

	@NotNull
	@NotEmpty
	private final String fromAccountId;

	@NotNull
	@NotEmpty
	private final String toAccountId;
	
	@NotNull
	@Min(value = 0, message = "Initial balance must be positive.")
	private BigDecimal balance;
	
	public TransactionBody(String fromAccountId, String toAccountId) {
		this.fromAccountId=fromAccountId;
		this.toAccountId=toAccountId;
		this.balance=BigDecimal.ZERO;
	}
	
	@JsonCreator
	  public TransactionBody(@JsonProperty("fromAccountId") String fromAccountId, 
			  @JsonProperty("toAccountId") String toAccountId,
	    @JsonProperty("balance") BigDecimal balance) {
	    this.fromAccountId = fromAccountId;
	    this.toAccountId=toAccountId;
	    this.balance = balance;
	  }
}
