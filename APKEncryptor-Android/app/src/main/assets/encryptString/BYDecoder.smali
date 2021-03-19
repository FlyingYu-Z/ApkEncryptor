.class public Lcom/beingyi/encrypt/BYDecoder;
.super Ljava/lang/Object;
.source "BYDecoder.java"


# static fields
.field public static s1:Ljava/lang/String;

.field public static s2:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 12
    const-string v0, "fff"

    sput-object v0, Lcom/beingyi/encrypt/BYDecoder;->s1:Ljava/lang/String;

    .line 13
    const-string v0, "fsghff"

    sput-object v0, Lcom/beingyi/encrypt/BYDecoder;->s2:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 10
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static decode()Ljava/lang/String;
    .registers 3

    .prologue
    .line 17
    const-string v1, ""

    .line 18
    .local v1, "result":Ljava/lang/String;
    const-string v0, "#pass"

    .line 20
    .local v0, "pass":Ljava/lang/String;
    sget-object v2, Lcom/beingyi/encrypt/StringPool;->s1:Ljava/lang/String;

    .line 22
    .local v2, "text":Ljava/lang/String;
    invoke-static {v2, v0}, Lcom/beingyi/encrypt/BYDecoder;->decode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 23
    return-object v1
.end method

.method public static #decode#(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .registers 12
    .param p0, "text"    # Ljava/lang/String;
    .param p1, "pass"    # Ljava/lang/String;

    .prologue
    .line 28
    invoke-virtual {p0}, Ljava/lang/String;->length()I

    move-result v3

    .line 30
    .local v3, "hexlen":I
    rem-int/lit8 v8, v3, 0x2

    const/4 v9, 0x1

    if-ne v8, v9, :cond_3a

    .line 32
    add-int/lit8 v3, v3, 0x1

    .line 33
    div-int/lit8 v8, v3, 0x2

    new-array v7, v8, [B

    .line 34
    .local v7, "result":[B
    new-instance v8, Ljava/lang/StringBuilder;

    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    const-string v9, "0"

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    invoke-virtual {v8, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p0

    .line 39
    :goto_22
    const/4 v5, 0x0

    .line 40
    .local v5, "j":I
    const/4 v4, 0x0

    .local v4, "i":I
    :goto_24
    if-ge v4, v3, :cond_3f

    .line 41
    add-int/lit8 v8, v4, 0x2

    invoke-virtual {p0, v4, v8}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v8

    const/16 v9, 0x10

    invoke-static {v8, v9}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;I)I

    move-result v8

    int-to-byte v8, v8

    aput-byte v8, v7, v5

    .line 42
    add-int/lit8 v5, v5, 0x1

    .line 40
    add-int/lit8 v4, v4, 0x2

    goto :goto_24

    .line 37
    .end local v4    # "i":I
    .end local v5    # "j":I
    .end local v7    # "result":[B
    :cond_3a
    div-int/lit8 v8, v3, 0x2

    new-array v7, v8, [B

    .restart local v7    # "result":[B
    goto :goto_22

    .line 46
    .restart local v4    # "i":I
    .restart local v5    # "j":I
    :cond_3f
    const/4 v1, 0x0

    .line 48
    .local v1, "decrypt":[B
    :try_start_40
    new-instance v6, Ljavax/crypto/spec/SecretKeySpec;

    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    move-result-object v8

    const-string v9, "AES"

    invoke-direct {v6, v8, v9}, Ljavax/crypto/spec/SecretKeySpec;-><init>([BLjava/lang/String;)V

    .line 49
    .local v6, "key":Ljava/security/Key;
    const-string v8, "AES"

    invoke-static {v8}, Ljavax/crypto/Cipher;->getInstance(Ljava/lang/String;)Ljavax/crypto/Cipher;

    move-result-object v0

    .line 50
    .local v0, "cipher":Ljavax/crypto/Cipher;
    const/4 v8, 0x2

    invoke-virtual {v0, v8, v6}, Ljavax/crypto/Cipher;->init(ILjava/security/Key;)V

    .line 51
    invoke-virtual {v0, v7}, Ljavax/crypto/Cipher;->doFinal([B)[B
    :try_end_58
    .catch Ljava/lang/Exception; {:try_start_40 .. :try_end_58} :catch_5f

    move-result-object v1

    .line 57
    .end local v0    # "cipher":Ljavax/crypto/Cipher;
    .end local v6    # "key":Ljava/security/Key;
    :goto_59
    new-instance v8, Ljava/lang/String;

    invoke-direct {v8, v1}, Ljava/lang/String;-><init>([B)V

    return-object v8

    .line 53
    :catch_5f
    move-exception v2

    .line 54
    .local v2, "e":Ljava/lang/Exception;
    invoke-virtual {v2}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_59
.end method
