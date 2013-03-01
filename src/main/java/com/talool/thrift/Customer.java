/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.talool.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer implements org.apache.thrift.TBase<Customer, Customer._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Customer");

  private static final org.apache.thrift.protocol.TField CUSTOMER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("customerId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField FIRST_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("firstName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField LAST_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("lastName", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField EMAIL_FIELD_DESC = new org.apache.thrift.protocol.TField("email", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField PASSWORD_FIELD_DESC = new org.apache.thrift.protocol.TField("password", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField ADDRESS_FIELD_DESC = new org.apache.thrift.protocol.TField("address", org.apache.thrift.protocol.TType.STRUCT, (short)6);
  private static final org.apache.thrift.protocol.TField CREATED_FIELD_DESC = new org.apache.thrift.protocol.TField("created", org.apache.thrift.protocol.TType.I64, (short)7);
  private static final org.apache.thrift.protocol.TField UPDATED_FIELD_DESC = new org.apache.thrift.protocol.TField("updated", org.apache.thrift.protocol.TType.I64, (short)8);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new CustomerStandardSchemeFactory());
    schemes.put(TupleScheme.class, new CustomerTupleSchemeFactory());
  }

  public long customerId; // required
  public String firstName; // required
  public String lastName; // required
  public String email; // required
  public String password; // required
  public Address address; // required
  public long created; // required
  public long updated; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CUSTOMER_ID((short)1, "customerId"),
    FIRST_NAME((short)2, "firstName"),
    LAST_NAME((short)3, "lastName"),
    EMAIL((short)4, "email"),
    PASSWORD((short)5, "password"),
    ADDRESS((short)6, "address"),
    CREATED((short)7, "created"),
    UPDATED((short)8, "updated");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // CUSTOMER_ID
          return CUSTOMER_ID;
        case 2: // FIRST_NAME
          return FIRST_NAME;
        case 3: // LAST_NAME
          return LAST_NAME;
        case 4: // EMAIL
          return EMAIL;
        case 5: // PASSWORD
          return PASSWORD;
        case 6: // ADDRESS
          return ADDRESS;
        case 7: // CREATED
          return CREATED;
        case 8: // UPDATED
          return UPDATED;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __CUSTOMERID_ISSET_ID = 0;
  private static final int __CREATED_ISSET_ID = 1;
  private static final int __UPDATED_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CUSTOMER_ID, new org.apache.thrift.meta_data.FieldMetaData("customerId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.FIRST_NAME, new org.apache.thrift.meta_data.FieldMetaData("firstName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LAST_NAME, new org.apache.thrift.meta_data.FieldMetaData("lastName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.EMAIL, new org.apache.thrift.meta_data.FieldMetaData("email", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PASSWORD, new org.apache.thrift.meta_data.FieldMetaData("password", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ADDRESS, new org.apache.thrift.meta_data.FieldMetaData("address", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Address.class)));
    tmpMap.put(_Fields.CREATED, new org.apache.thrift.meta_data.FieldMetaData("created", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields.UPDATED, new org.apache.thrift.meta_data.FieldMetaData("updated", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Customer.class, metaDataMap);
  }

  public Customer() {
  }

  public Customer(
    long customerId,
    String firstName,
    String lastName,
    String email,
    String password,
    Address address,
    long created,
    long updated)
  {
    this();
    this.customerId = customerId;
    setCustomerIdIsSet(true);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.address = address;
    this.created = created;
    setCreatedIsSet(true);
    this.updated = updated;
    setUpdatedIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Customer(Customer other) {
    __isset_bitfield = other.__isset_bitfield;
    this.customerId = other.customerId;
    if (other.isSetFirstName()) {
      this.firstName = other.firstName;
    }
    if (other.isSetLastName()) {
      this.lastName = other.lastName;
    }
    if (other.isSetEmail()) {
      this.email = other.email;
    }
    if (other.isSetPassword()) {
      this.password = other.password;
    }
    if (other.isSetAddress()) {
      this.address = new Address(other.address);
    }
    this.created = other.created;
    this.updated = other.updated;
  }

  public Customer deepCopy() {
    return new Customer(this);
  }

  public void clear() {
    setCustomerIdIsSet(false);
    this.customerId = 0;
    this.firstName = null;
    this.lastName = null;
    this.email = null;
    this.password = null;
    this.address = null;
    setCreatedIsSet(false);
    this.created = 0;
    setUpdatedIsSet(false);
    this.updated = 0;
  }

  public long getCustomerId() {
    return this.customerId;
  }

  public Customer setCustomerId(long customerId) {
    this.customerId = customerId;
    setCustomerIdIsSet(true);
    return this;
  }

  public void unsetCustomerId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __CUSTOMERID_ISSET_ID);
  }

  /** Returns true if field customerId is set (has been assigned a value) and false otherwise */
  public boolean isSetCustomerId() {
    return EncodingUtils.testBit(__isset_bitfield, __CUSTOMERID_ISSET_ID);
  }

  public void setCustomerIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __CUSTOMERID_ISSET_ID, value);
  }

  public String getFirstName() {
    return this.firstName;
  }

  public Customer setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public void unsetFirstName() {
    this.firstName = null;
  }

  /** Returns true if field firstName is set (has been assigned a value) and false otherwise */
  public boolean isSetFirstName() {
    return this.firstName != null;
  }

  public void setFirstNameIsSet(boolean value) {
    if (!value) {
      this.firstName = null;
    }
  }

  public String getLastName() {
    return this.lastName;
  }

  public Customer setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public void unsetLastName() {
    this.lastName = null;
  }

  /** Returns true if field lastName is set (has been assigned a value) and false otherwise */
  public boolean isSetLastName() {
    return this.lastName != null;
  }

  public void setLastNameIsSet(boolean value) {
    if (!value) {
      this.lastName = null;
    }
  }

  public String getEmail() {
    return this.email;
  }

  public Customer setEmail(String email) {
    this.email = email;
    return this;
  }

  public void unsetEmail() {
    this.email = null;
  }

  /** Returns true if field email is set (has been assigned a value) and false otherwise */
  public boolean isSetEmail() {
    return this.email != null;
  }

  public void setEmailIsSet(boolean value) {
    if (!value) {
      this.email = null;
    }
  }

  public String getPassword() {
    return this.password;
  }

  public Customer setPassword(String password) {
    this.password = password;
    return this;
  }

  public void unsetPassword() {
    this.password = null;
  }

  /** Returns true if field password is set (has been assigned a value) and false otherwise */
  public boolean isSetPassword() {
    return this.password != null;
  }

  public void setPasswordIsSet(boolean value) {
    if (!value) {
      this.password = null;
    }
  }

  public Address getAddress() {
    return this.address;
  }

  public Customer setAddress(Address address) {
    this.address = address;
    return this;
  }

  public void unsetAddress() {
    this.address = null;
  }

  /** Returns true if field address is set (has been assigned a value) and false otherwise */
  public boolean isSetAddress() {
    return this.address != null;
  }

  public void setAddressIsSet(boolean value) {
    if (!value) {
      this.address = null;
    }
  }

  public long getCreated() {
    return this.created;
  }

  public Customer setCreated(long created) {
    this.created = created;
    setCreatedIsSet(true);
    return this;
  }

  public void unsetCreated() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __CREATED_ISSET_ID);
  }

  /** Returns true if field created is set (has been assigned a value) and false otherwise */
  public boolean isSetCreated() {
    return EncodingUtils.testBit(__isset_bitfield, __CREATED_ISSET_ID);
  }

  public void setCreatedIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __CREATED_ISSET_ID, value);
  }

  public long getUpdated() {
    return this.updated;
  }

  public Customer setUpdated(long updated) {
    this.updated = updated;
    setUpdatedIsSet(true);
    return this;
  }

  public void unsetUpdated() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __UPDATED_ISSET_ID);
  }

  /** Returns true if field updated is set (has been assigned a value) and false otherwise */
  public boolean isSetUpdated() {
    return EncodingUtils.testBit(__isset_bitfield, __UPDATED_ISSET_ID);
  }

  public void setUpdatedIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __UPDATED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case CUSTOMER_ID:
      if (value == null) {
        unsetCustomerId();
      } else {
        setCustomerId((Long)value);
      }
      break;

    case FIRST_NAME:
      if (value == null) {
        unsetFirstName();
      } else {
        setFirstName((String)value);
      }
      break;

    case LAST_NAME:
      if (value == null) {
        unsetLastName();
      } else {
        setLastName((String)value);
      }
      break;

    case EMAIL:
      if (value == null) {
        unsetEmail();
      } else {
        setEmail((String)value);
      }
      break;

    case PASSWORD:
      if (value == null) {
        unsetPassword();
      } else {
        setPassword((String)value);
      }
      break;

    case ADDRESS:
      if (value == null) {
        unsetAddress();
      } else {
        setAddress((Address)value);
      }
      break;

    case CREATED:
      if (value == null) {
        unsetCreated();
      } else {
        setCreated((Long)value);
      }
      break;

    case UPDATED:
      if (value == null) {
        unsetUpdated();
      } else {
        setUpdated((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case CUSTOMER_ID:
      return Long.valueOf(getCustomerId());

    case FIRST_NAME:
      return getFirstName();

    case LAST_NAME:
      return getLastName();

    case EMAIL:
      return getEmail();

    case PASSWORD:
      return getPassword();

    case ADDRESS:
      return getAddress();

    case CREATED:
      return Long.valueOf(getCreated());

    case UPDATED:
      return Long.valueOf(getUpdated());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case CUSTOMER_ID:
      return isSetCustomerId();
    case FIRST_NAME:
      return isSetFirstName();
    case LAST_NAME:
      return isSetLastName();
    case EMAIL:
      return isSetEmail();
    case PASSWORD:
      return isSetPassword();
    case ADDRESS:
      return isSetAddress();
    case CREATED:
      return isSetCreated();
    case UPDATED:
      return isSetUpdated();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Customer)
      return this.equals((Customer)that);
    return false;
  }

  public boolean equals(Customer that) {
    if (that == null)
      return false;

    boolean this_present_customerId = true;
    boolean that_present_customerId = true;
    if (this_present_customerId || that_present_customerId) {
      if (!(this_present_customerId && that_present_customerId))
        return false;
      if (this.customerId != that.customerId)
        return false;
    }

    boolean this_present_firstName = true && this.isSetFirstName();
    boolean that_present_firstName = true && that.isSetFirstName();
    if (this_present_firstName || that_present_firstName) {
      if (!(this_present_firstName && that_present_firstName))
        return false;
      if (!this.firstName.equals(that.firstName))
        return false;
    }

    boolean this_present_lastName = true && this.isSetLastName();
    boolean that_present_lastName = true && that.isSetLastName();
    if (this_present_lastName || that_present_lastName) {
      if (!(this_present_lastName && that_present_lastName))
        return false;
      if (!this.lastName.equals(that.lastName))
        return false;
    }

    boolean this_present_email = true && this.isSetEmail();
    boolean that_present_email = true && that.isSetEmail();
    if (this_present_email || that_present_email) {
      if (!(this_present_email && that_present_email))
        return false;
      if (!this.email.equals(that.email))
        return false;
    }

    boolean this_present_password = true && this.isSetPassword();
    boolean that_present_password = true && that.isSetPassword();
    if (this_present_password || that_present_password) {
      if (!(this_present_password && that_present_password))
        return false;
      if (!this.password.equals(that.password))
        return false;
    }

    boolean this_present_address = true && this.isSetAddress();
    boolean that_present_address = true && that.isSetAddress();
    if (this_present_address || that_present_address) {
      if (!(this_present_address && that_present_address))
        return false;
      if (!this.address.equals(that.address))
        return false;
    }

    boolean this_present_created = true;
    boolean that_present_created = true;
    if (this_present_created || that_present_created) {
      if (!(this_present_created && that_present_created))
        return false;
      if (this.created != that.created)
        return false;
    }

    boolean this_present_updated = true;
    boolean that_present_updated = true;
    if (this_present_updated || that_present_updated) {
      if (!(this_present_updated && that_present_updated))
        return false;
      if (this.updated != that.updated)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(Customer other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Customer typedOther = (Customer)other;

    lastComparison = Boolean.valueOf(isSetCustomerId()).compareTo(typedOther.isSetCustomerId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCustomerId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.customerId, typedOther.customerId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFirstName()).compareTo(typedOther.isSetFirstName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFirstName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.firstName, typedOther.firstName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLastName()).compareTo(typedOther.isSetLastName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLastName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.lastName, typedOther.lastName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEmail()).compareTo(typedOther.isSetEmail());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEmail()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.email, typedOther.email);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPassword()).compareTo(typedOther.isSetPassword());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPassword()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.password, typedOther.password);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAddress()).compareTo(typedOther.isSetAddress());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddress()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.address, typedOther.address);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCreated()).compareTo(typedOther.isSetCreated());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCreated()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.created, typedOther.created);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUpdated()).compareTo(typedOther.isSetUpdated());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUpdated()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.updated, typedOther.updated);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Customer(");
    boolean first = true;

    sb.append("customerId:");
    sb.append(this.customerId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("firstName:");
    if (this.firstName == null) {
      sb.append("null");
    } else {
      sb.append(this.firstName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("lastName:");
    if (this.lastName == null) {
      sb.append("null");
    } else {
      sb.append(this.lastName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("email:");
    if (this.email == null) {
      sb.append("null");
    } else {
      sb.append(this.email);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("password:");
    if (this.password == null) {
      sb.append("null");
    } else {
      sb.append(this.password);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("address:");
    if (this.address == null) {
      sb.append("null");
    } else {
      sb.append(this.address);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("created:");
    sb.append(this.created);
    first = false;
    if (!first) sb.append(", ");
    sb.append("updated:");
    sb.append(this.updated);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (firstName == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'firstName' was not present! Struct: " + toString());
    }
    if (lastName == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'lastName' was not present! Struct: " + toString());
    }
    if (email == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'email' was not present! Struct: " + toString());
    }
    if (password == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'password' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (address != null) {
      address.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te.getMessage());
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te.getMessage());
    }
  }

  private static class CustomerStandardSchemeFactory implements SchemeFactory {
    public CustomerStandardScheme getScheme() {
      return new CustomerStandardScheme();
    }
  }

  private static class CustomerStandardScheme extends StandardScheme<Customer> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Customer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // CUSTOMER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.customerId = iprot.readI64();
              struct.setCustomerIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // FIRST_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.firstName = iprot.readString();
              struct.setFirstNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // LAST_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.lastName = iprot.readString();
              struct.setLastNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // EMAIL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.email = iprot.readString();
              struct.setEmailIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // PASSWORD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.password = iprot.readString();
              struct.setPasswordIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // ADDRESS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.address = new Address();
              struct.address.read(iprot);
              struct.setAddressIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // CREATED
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.created = iprot.readI64();
              struct.setCreatedIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // UPDATED
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.updated = iprot.readI64();
              struct.setUpdatedIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Customer struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(CUSTOMER_ID_FIELD_DESC);
      oprot.writeI64(struct.customerId);
      oprot.writeFieldEnd();
      if (struct.firstName != null) {
        oprot.writeFieldBegin(FIRST_NAME_FIELD_DESC);
        oprot.writeString(struct.firstName);
        oprot.writeFieldEnd();
      }
      if (struct.lastName != null) {
        oprot.writeFieldBegin(LAST_NAME_FIELD_DESC);
        oprot.writeString(struct.lastName);
        oprot.writeFieldEnd();
      }
      if (struct.email != null) {
        oprot.writeFieldBegin(EMAIL_FIELD_DESC);
        oprot.writeString(struct.email);
        oprot.writeFieldEnd();
      }
      if (struct.password != null) {
        oprot.writeFieldBegin(PASSWORD_FIELD_DESC);
        oprot.writeString(struct.password);
        oprot.writeFieldEnd();
      }
      if (struct.address != null) {
        oprot.writeFieldBegin(ADDRESS_FIELD_DESC);
        struct.address.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(CREATED_FIELD_DESC);
      oprot.writeI64(struct.created);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(UPDATED_FIELD_DESC);
      oprot.writeI64(struct.updated);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CustomerTupleSchemeFactory implements SchemeFactory {
    public CustomerTupleScheme getScheme() {
      return new CustomerTupleScheme();
    }
  }

  private static class CustomerTupleScheme extends TupleScheme<Customer> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Customer struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.firstName);
      oprot.writeString(struct.lastName);
      oprot.writeString(struct.email);
      oprot.writeString(struct.password);
      BitSet optionals = new BitSet();
      if (struct.isSetCustomerId()) {
        optionals.set(0);
      }
      if (struct.isSetAddress()) {
        optionals.set(1);
      }
      if (struct.isSetCreated()) {
        optionals.set(2);
      }
      if (struct.isSetUpdated()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetCustomerId()) {
        oprot.writeI64(struct.customerId);
      }
      if (struct.isSetAddress()) {
        struct.address.write(oprot);
      }
      if (struct.isSetCreated()) {
        oprot.writeI64(struct.created);
      }
      if (struct.isSetUpdated()) {
        oprot.writeI64(struct.updated);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Customer struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.firstName = iprot.readString();
      struct.setFirstNameIsSet(true);
      struct.lastName = iprot.readString();
      struct.setLastNameIsSet(true);
      struct.email = iprot.readString();
      struct.setEmailIsSet(true);
      struct.password = iprot.readString();
      struct.setPasswordIsSet(true);
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.customerId = iprot.readI64();
        struct.setCustomerIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.address = new Address();
        struct.address.read(iprot);
        struct.setAddressIsSet(true);
      }
      if (incoming.get(2)) {
        struct.created = iprot.readI64();
        struct.setCreatedIsSet(true);
      }
      if (incoming.get(3)) {
        struct.updated = iprot.readI64();
        struct.setUpdatedIsSet(true);
      }
    }
  }

}

