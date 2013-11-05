package compiler.IR;

import java.util.HashSet;
import java.util.LinkedList;
import compiler.PrettyPrinter;
import compiler.Exceptions.ClassErrorMethod;
import compiler.Exceptions.ClassNotFound;
import compiler.Exceptions.MethodNotFound;
import compiler.Exceptions.TypeCheckerException;
import compiler.Exceptions.VariableNotFound;

public class MJMethodCallExpr extends MJExpression {

	private MJIdentifier method;
	private LinkedList<MJExpression> arglist;
	private MJMethod target;

	public MJMethodCallExpr(MJIdentifier m, LinkedList<MJExpression> arglist) {
		this.method = m;
		this.arglist = arglist;
	}

	public void prettyPrint(PrettyPrinter prepri) {
		boolean first = true;

		this.method.prettyPrint(prepri);
		prepri.print("(");
		for (MJExpression arg : arglist) {

			if (!first) {
				prepri.print(", ");
			} else {
				first = false;
			}

			arg.prettyPrint(prepri);
		}
		prepri.print(")");
	}

	MJType typeCheck() throws TypeCheckerException {

		MJClass decl = null;
		MJClass cla = IR.currentClass;
		MJIdentifier ident = this.method;
		MJMethod meth;

		if(ident instanceof MJSelector){

			MJSelector sel = (MJSelector) ident;
			MJType type = sel.getObject().typeCheck();

			if(ident.getType().isClass()){
				decl = sel.getObject().typeCheck().getDecl();
				cla = type.getDecl();
				ident = sel.getField();
			}else throw	new TypeCheckerException(ident.getName()+" is not a Class");
		}else {
			if(ident.getName().equals("this"))
			{
				// Something should happen here
			}else if (ident.getName().equals("super"))
			{
				// Something should happen here
			}
		}
		for(MJExpression args : arglist)
		{
			args.typeCheck();
		}

		try {
			meth = IR.classes.lookupMethod(decl, cla.getName(), arglist);
		} catch (ClassErrorMethod e) {
			throw new TypeCheckerException(e.getMessage());
		} catch (MethodNotFound e) {
			throw new TypeCheckerException(e.getMessage());
		}
		this.type = meth.getReturnType();
		this.target = meth;
		return this.type;
	}

	void variableInit(HashSet<MJVariable> initialized)
			throws TypeCheckerException {

		this.method.variableInit(initialized);
		
		for(MJExpression arg : arglist){
			arg.variableInit(initialized);
		}
	}
}
