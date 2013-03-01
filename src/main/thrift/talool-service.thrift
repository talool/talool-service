
namespace java com.talool.thrift

typedef i64 Timestamp

exception ServiceException {
  1: i32 errorCode,
  2: string errorDesc
}

struct Merchant {
  1: required string name;
  2: required string email;
}

struct Address {
  1: i64 addressId;
  2: required string address1;
  3: string address2;
  4: string city;
  5: string stateProvinceCounty;
  6: string zip;
  7: string country;
  8: Timestamp created;
  9: Timestamp updated;
}

struct Customer {
  1: i64 customerId;
  2: required string firstName;
  3: required string lastName;
  4: required string email;
  5: required string password;
  6: Address address;
  7: Timestamp created;
  8: Timestamp updated;
}
 
service TaloolService {
   void registerCustomer(1:Customer customer,2:string password) throws (1:ServiceException error);
}