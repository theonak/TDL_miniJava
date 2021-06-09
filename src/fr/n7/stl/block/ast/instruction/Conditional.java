/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import java.util.Optional;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.type.*;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collect(HierarchicalScope<Declaration> _scope) {
        if (this.elseBranch != null) {
		    return condition.collect(_scope) && thenBranch.collect(_scope) && elseBranch.collect(_scope);
        } else {
            return condition.collect(_scope) && thenBranch.collect(_scope);
        }
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		if (this.elseBranch != null) {
		    return condition.resolve(_scope) && thenBranch.resolve(_scope) && elseBranch.resolve(_scope);
        } else {
            return condition.resolve(_scope) && thenBranch.resolve(_scope);
        }
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if (this.elseBranch != null) {
			return thenBranch.checkType() && elseBranch.checkType() && condition.getType().equalsTo(AtomicType.BooleanType);
        } else {
            return thenBranch.checkType() && condition.getType().equalsTo(AtomicType.BooleanType);
        }
		
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.thenBranch.allocateMemory(_register,_offset);
		if(elseBranch !=null)
			elseBranch.allocateMemory(_register,_offset);
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		int idCond = _factory.createLabelNumber();
		frag.append(this.condition.getCode(_factory));
		if(elseBranch ==null) {
			frag.add(_factory.createJumpIf("EndConditional"+ idCond,0));
		} else {
			frag.add(_factory.createJumpIf("elseBranch"+ idCond,0));
		}
		frag.append(thenBranch.getCode(_factory));
		if(elseBranch !=null) {
			frag.add(_factory.createJump("EndConditional"+idCond));
			frag.addSuffix("elseBranch"+idCond);
			frag.append(elseBranch.getCode(_factory));
		}
		frag.addSuffix("EndConditional"+idCond);
		return frag;
	}

}
