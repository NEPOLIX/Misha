# MishaGlobal
This module contains common methods and classes of Misha library.

#### Security
##### RSA
In `RSA` class setup `PRIVATE_KEY` and `PUBLIC_KEY` with the following commands
```
openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:8192
openssl rsa -pubout -in private.pem -out public.pem
```
Sample out put
PRIVATE_KEY:
-----BEGIN PRIVATE KEY-----
MIIkQQIBADANBgkqhkiG9w0BAQEFAASCJCswgiQnAgEAAoIIAQCxJQ2fPHhD1tmb
...
-----END PRIVATE KEY-----

PUBLIC_KEY:
-----BEGIN PUBLIC KEY-----
MIIIIjANBgkqhkiG9w0BAQEFAAOCCA8AMIIICgKCCAEAsSUNnzx4Q9bZm2Bi0yFz
...
-----END PUBLIC KEY-----

##### AES
Set `AES.GLOBAL_AES_KEY` to any random string with length of 16.
`AES.GLOBAL_AES_KEY` must be unique and can't be changed later as it is used in various modules including [MishaDB](https://github.com/NEPOLIX/Misha/tree/master/MishaDB)