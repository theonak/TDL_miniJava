/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a repetition instruction.
 * @author Marc Pantel
 *
 */
public class Repetition implements Instruction {

	protected Expression condition;
	protected Block body;
	protected SymbolTable tds;
	public Repetition(Expression _condition, Block _body) {
		this.condition = _condition;
		this.body = _body;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "while (" + this.condition + ") " + this.body;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collect(HierarchicalScope<Declaration> _scope) {
		this.tds = new SymbolTable(_scope);
		return condition.collect(_scope) && body.collect(tds);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return condition.resolve(_scope) && body.resolve(_scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return body.checkType() && condition.getType().equalsTo(AtomicType.BooleanType);
	}


	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.body.allocateMemory(_register,_offset);
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragment = _factory.createFragment();
		int id = _factory.createLabelNumber();
		fragment.append(condition.getCode(_factory));
		fragment.addPrefix("while"+id);
		fragment.add(_factory.createJumpIf("End while"+id,0));
		fragment.append(body.getCode(_factory));
		fragment.add(_factory.createJump("while"+id));
		fragment.addSuffix("End while"+id);
		return fragment;
	}

}
