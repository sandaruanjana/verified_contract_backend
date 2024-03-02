package com.wixis360.verifiedcontractingbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {
	private String type = "Bearer";
	private String token;
	private long expiresIn;

	public TokenResponse(String accessToken, long expiresIn) {
		this.token = accessToken;
		this.expiresIn = expiresIn;
	}

	@JsonProperty("access_token")
	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	@JsonProperty("token_type")
	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	@JsonProperty("expires_in")
	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
