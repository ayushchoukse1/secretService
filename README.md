Welcome to Secret Service API!
=========================

Author: [Ayush Choukse](https://github.com/ayushchoukse1)

**[Click Here for detailed api documentation](https://secretservice.herokuapp.com/)**

# [Overview](#overview)

The secret service API can be used by a user to create secrets of his own. Rest calls are made to perform any operation with the API. The following are the operations a user can perform:

*   **Create a user Account.**

    *   User authority is provided by default on account creation.


*   **Get authorization with the API.**


    *   Get access token to make further operations on the resources.


*   **Update, and delete the user account details.**



    *   Only the user can perform these operations on its account.

    *   Authorization token is needed in order to perform these operations.

    *   No other user can see your account details, update or delete it.

*   **Create, update, and delete a secret.**


    *   After getting authorized, a user can create a secret, and perform further operations on a secrete by providing access token in the header.

    *   All the secrets are by default encrypted in database.

    *   No other user can access, delete or update your secret.

    *   User with Administrator rights also cannot see any secrets created by any other user.
    
    ### **Tables**

Following are the URI listings on which REST calls are made to the API.

URI     | Description
-------- | ---
**/oauth/token** | Used to get access token for authorization.
**/oauth/refresh** |Used to get refresh token after access token expires.
**/user** | Used to create user using POST request.
**/user/{id}** |Used to get, update, and delete a user.
**/secret**|Used to create secret using POST request.
**/secrets** |Used to get list of all the secrets using GET request.
**/secret/{id}** |Used to get, update, and delete a secret.
