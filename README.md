## Lunagram (ALPHA VERSION)

Lunagram is a messenger application based on Telegram with a  built-in Cosmos wallet. Lunagram wallet supports Cosmos Atom transfers, staking, and governance.

Users can link their Telegram account to a Cosmos wallet by setting their bio as their Cosmos address.

Note: By setting your bio as your Cosmos account, your Cosmos address becomes public. Please take precautions regarding dangers this may entail.

Also, Lunagram wallet implements a transfer request via LMI(Lunamint Message Interface). Bot developers can integrate Cosmos payments by using our standard interface explained below.

## Lunagram Message Interface, LMI

### Introduction

Lunamint Message Interface, of LMI for short, is a way for Telegram bots or applications to request payments through the Lunagram application.

When a LMI message is sent to a user with the Lunagram application, the Lunagram application creates an inline instance button in the chat. The receiver can then process payments or sign a message by pressing the button.  

### LMI Format (for LMI version 1)

**Sample LMI Message**

```
lmi::1::MX+MCwHCi98HZm/4Zxk03dhX18/opY3CdL2/llztzDZJkMxwm3vXbBArnrxpmxS3l8UT3r4Ya4EDBu9wuL1owE2CkybJxuhLJn9hf13M1/VayKJLH0GLaekQmGtm1wLEh1PYd4Ne3OZp+/fWAQITjoOfffiFlwlY4htSl5h2zQHQ6QulURGnwf4iR2TWJKnXAM46v1EL9qOOkIU5TfBdNHLPqyDdgvdawDKcZjbx2M0W3bpZiPxDYASBVuyAxFR2MFfnidRnQ5neS1thW1vYRhjLi5aOyXAUHtUSXhNHCElnOxKzTI7zfd2vFB/k31pLqRgcAMUuw7fgGZilSVoFX2CdFn8gcKNVEA6STXU+lHMV2OxvFsnwBoMhSv9mBxsloJot+Q3inj3fV5LX+5FT43MpsHAxeCR26YNtTCjafSU=
```

The LMI message above is divided into three parts: `lmi(prefix)::1(version)::message(encrypted)`

**Note**
1. The format of the message must follow the above standard.
2. Message must be encrypted with AES/CBC/PKCS5Padding. (Encryption key = `lunagram`, Encryption iv = `evqndl&wgvhvaoz!`)

Please follow the format below before encrypting the message:

```{
 "action": "send",
 "requester_t_id": “lunagrambot”,
 "tx": {
  “from”: "cosmos17v0fff40qfwp8l8ruhyjuvh39t8j7qarhkwjpd”,
  "to": "cosmos17v0fff40qfwp8l8ruhyjuvh39t8j7qarhkwjpd",
  "denom": “stake”,
  "amount": “10”,
  "memo": “blah~blah~”
 },
 "callback": {
  "url": "https://lunatestcallback/",
  "endpoint": "deposit“,
  "custom_fields": {
   “custom_field1”: “It’s custom field1”,
   “custom_field2”: “It’s custom field2”
  }
 }
}
```

Field | Type | Description 
------|------|---------
action | String | `send`
requester_t_id | String | Telegram ID the person sending the LMI message
tx.from | String | (Optional) Cosmos address sending the payment
tx.to | String | Cosmos address receiving the payment
tx.denom | String | Token demonination
tx.amount | String | Token amount 
tx.memo | String | (Optional) Memo field of the transaction
callback.url | String | The callback base url that will be receiving the callback after the user's action has been completed. Must end in `/`. Callbacks only support https.
callback.endpoint | String | Endpoint of the callback url. Please input the full path such as `usr/deposit`
callback.custom_field | json object / any type | Lunagram sends the callback defined in this field after the user's action has completed.


### LMI Callback

**HTTPS / POST / Content-Type: application/json**

After the user has completed the action, Lunagram application sends a callback with the `custom_field` to the url defined with the message `lmi_version:1`.

If the message included in the `custom_field` is as follows:

```
"custom_field": {
 “custom_field1”: “It’s custom field1”,
 “custom_field2”: “It’s custom field2”
}
```

The data to be received from the callback should look like this:

```
{
 “lmi_version”:1,
 “custom_field1”: “It’s custom field1”,
 “custom_field2”: “It’s custom field2”
}
```

### WARNING

Callbacks do not guarantee the success of the transaction. In case of a transfer, please refer to callbacks only as a double-checking measure. The best way to confirm a transaction is through checking the blockchain data.
