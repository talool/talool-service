package com.talool.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.Customer;
import com.talool.core.Sex;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.social.CustomerSocialAccountImpl;

@Entity
@Table(name = "customer", catalog = "public")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class CustomerImpl implements Customer
{
	private static final Logger LOG = LoggerFactory.getLogger(CustomerImpl.class);

	private static final long serialVersionUID = 2498058366640693644L;

	@Id
	@GenericGenerator(name = "uuid_gen", strategy = "com.talool.hibernate.UUIDGenerator")
	@GeneratedValue(generator = "uuid_gen")
	@Type(type = "pg-uuid")
	@Column(name = "customer_id", unique = true, nullable = false)
	private UUID id;

	@Type(type = "sexType")
	@Column(name = "sex_t", nullable = true, columnDefinition = "sex_type")
	private Sex sex;

	@Column(name = "birth_date", unique = false, nullable = true)
	private Date birthDate;

	@Column(name = "first_name", unique = false, nullable = true, length = 64)
	private String firstName;

	@Column(name = "last_name", unique = false, nullable = true, length = 64)
	private String lastName;

	@Column(name = "email", unique = true, nullable = true, length = 128)
	private String email;

	@Column(name = "password", unique = false, nullable = false, length = 64)
	private String password;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = CustomerSocialAccountImpl.class, orphanRemoval = true)
	@MapKey(name = "socialNetwork")
	@JoinColumn(name = "customer_id")
	private final Map<SocialNetwork, CustomerSocialAccount> socialAccounts = new HashMap<SocialNetwork, CustomerSocialAccount>();

	@Embedded
	private CreatedUpdated createdUpdated;

	@Override
	public UUID getId()
	{
		return id;
	}

	@Override
	public String getFirstName()
	{
		return firstName;
	}

	@Override
	public String getLastName()
	{
		return lastName;
	}

	@Override
	public String getEmail()
	{
		return email;
	}

	@Override
	public Date getCreated()
	{
		return createdUpdated.getCreated();
	}

	@Override
	public Date getUpdated()
	{
		return createdUpdated.getUpdated();
	}

	@Override
	public Date getBirthDate()
	{
		return birthDate;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof CustomerImpl))
		{
			return false;
		}

		final CustomerImpl other = (CustomerImpl) obj;

		if (getId() != other.getId())
		{
			return false;
		}

		return new EqualsBuilder().append(getFirstName(), other.getFirstName())
				.append(getLastName(), other.getLastName()).append(getEmail(), other.getEmail()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getFirstName()).append(getLastName())
				.append(getEmail()).hashCode();
	}

	@Override
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	@Override
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	@Override
	public void setEmail(String email)
	{
		this.email = email;
	}

	@Override
	public void setPassword(String password)
	{
		this.password = password;
	}

	@Override
	public Sex getSex()
	{
		return sex;
	}

	@Override
	public void setSex(Sex sex)
	{
		this.sex = sex;
	}

	@Override
	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}

	@Override
	public Map<SocialNetwork, CustomerSocialAccount> getSocialAccounts()
	{
		return socialAccounts;
	}

	@Override
	public void addSocialAccount(final CustomerSocialAccount socialAccount)
	{
		socialAccounts.put(socialAccount.getSocialNetwork(), socialAccount);
	}

	@Override
	public void removeSocialAccount(final CustomerSocialAccount socialAccount)
	{
		socialAccounts.remove(socialAccount.getSocialNetwork());

	}

	@Override
	public String getFullName()
	{
		return firstName + " " + lastName;
	}

}