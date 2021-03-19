package com.fly.apkencryptor.axmleditor.decode;

public interface IVisitor {
	public void visit(BNSNode node);
	public void visit(BTagNode node);
	public void visit(BTXTNode node);
}
