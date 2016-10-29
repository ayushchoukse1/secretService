package com.user.secrets.dao;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "secret")
public class Secret {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String title;

	private String body;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable=false)
	@CreationTimestamp
	private Date createdOn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date updatedOn;
	
	@OneToMany(targetEntity = User.class, cascade = CascadeType.ALL)
	private User user;
}
