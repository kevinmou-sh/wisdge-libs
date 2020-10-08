package com.wisdge.commons.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MailSender {
	private String host;
	private int port;
	private String name;
	private String from;
	private boolean auth;
	private String user;
	private String pwd;
}
