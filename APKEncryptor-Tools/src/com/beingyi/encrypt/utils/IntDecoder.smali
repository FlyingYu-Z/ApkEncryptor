.class public Lcom/beingyi/encrypt/utils/IntDecoder;
.super Ljava/lang/Object;
.source "IntDecoder.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static decode(I)I
    .registers 2
    .param p0, "value"    # I

    .prologue
    .line 11
    add-int/lit16 v0, p0, -0x3e7

    return v0
.end method

.method public static encode(I)I
    .registers 2
    .param p0, "value"    # I

    .prologue
    .line 15
    add-int/lit16 v0, p0, 0x3e7

    return v0
.end method
