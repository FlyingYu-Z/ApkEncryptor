
.method public static #decode()Ljava/lang/String;
    .registers 3

    .prologue
    const-string v1, ""

    #.local v1, "result":Ljava/lang/String;
    const-string v0, "#pass"

    #.local v0, "pass":Ljava/lang/String;
    sget-object v2, Lcom/beingyi/encrypt/StringPool;->#field:Ljava/lang/String;

    #.local v2, "text":Ljava/lang/String;
    invoke-static {v2, v0}, Lcom/beingyi/encrypt/BYDecoder;->#decode#(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    return-object v1
.end method
