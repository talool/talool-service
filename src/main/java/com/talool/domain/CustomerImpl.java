package com.talool.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import com.talool.core.Customer;
import com.talool.core.Sex;
import com.talool.core.SocialAccount;
import com.talool.core.SocialNetwork;
import com.talool.persistence.GenericEnumUserType;

@Entity
@Table(name = "customer", catalog = "public")
@TypeDef(name = "sexType", typeClass = GenericEnumUserType.class, parameters = {
		@Parameter(name = "enumClass", value = "com.talool.core.Sex"),
		@Parameter(name = "identifierMethod", value = "getLetter"), @Parameter(name = "valueOfMethod", value = "valueByLetter") })
public class CustomerImpl implements Customer
{
	private static final long serialVersionUID = 2498058366640693644L;

	@Id
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_customer_seq")
	@SequenceGenerator(name = "my_customer_seq", sequenceName = "customer_customer_id_seq")
	@Column(name = "customer_id", unique = true, nullable = false)
	private Long id;

	@Type(type = "sexType")
	// @Column(name = "sex_t", columnDefinition = "sex_type")
	@Column(name = "sex_t", nullable = true, columnDefinition = "sex_type")
	private Sex sex;

	@Column(name = "birth_date", unique = false, nullable = true)
	private Date birthDate;

	@Column(name = "first_name", unique = false, nullable = true, length = 64)
	private String firstName;

	@Column(name = "last_name", unique = false, nullable = true, length = 64)
	private String lastName;

	@Column(name = "email", unique = true, nullable = true, length = 64)
	private String email;

	@Column(name = "password", unique = false, nullable = false, length = 64)
	private String password;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = SocialAccountImpl.class, mappedBy = "primaryKey.userId")
	/*
	 * TODO - sucks - cant get a formula to work. Replace WHERE clause with
	 * something dynamic that reads the enum to prevent any future bug (WHERE has
	 * to be constant!)
	 */
	@Where(clause = "account_t='CUS'")
	@MapKey(name = "primaryKey.socialNetwork")
	private final Map<SocialNetwork, SocialAccount> socialAccounts = new HashMap<SocialNetwork, SocialAccount>();

	@Embedded
	private CreatedUpdated createdUpdated;

	@Override
	public Long getId()
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
	@Access(AccessType.PROPERTY)
	@Column(name = "birth_date", insertable = true, updatable = true, nullable = true)
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

		return new EqualsBuilder().append(getFirstName(), other.getFirstName()).append(getLastName(), other.getLastName())
				.append(getEmail(), other.getEmail()).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37).append(getFirstName()).append(getLastName()).append(getEmail()).hashCode();
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
	public Map<SocialNetwork, SocialAccount> getSocialAccounts()
	{
		return socialAccounts;
	}

	@Override
	public void addSocialAccount(final SocialAccount socialAccount)
	{
		socialAccounts.put(socialAccount.getSocialNetwork(), socialAccount);
	}

}