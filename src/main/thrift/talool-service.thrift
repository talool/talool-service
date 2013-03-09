
namespace java com.talool.thrift

typedef i64 Timestamp

enum TSex { M,F,U }

exception TServiceException {
  1: i32 errorCode,
  2: string errorDesc
}

struct TMerchant {
  1: required string name;
  2: required string email;
}

struct TAddress {
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

struct TCustomer {
  1: i64 customerId;
  2: required string firstName;
  3: required string lastName;
  4: required string email;
  6: TSex sex;
  7: Timestamp created;
  8: Timestamp updated;
}
 
service TaloolService {
   void registerCustomer(1:TCustomer customer,2:string password) throws (1:TServiceException error);
   TCustomer authCustomer(1:string email,2:string password) throws (1:TServiceException error);
}