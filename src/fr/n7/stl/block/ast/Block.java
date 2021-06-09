/**
 * 
 */
package fr.n7.stl.block.ast;

import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.declaration.*;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.type.declaration.*;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.miniJava.definition.ThisAssignement;
import fr.n7.stl.block.ast.type.*;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Represents a Block node in the Abstract Syntax Tree node for the Bloc language.
 * Declares the various semantics attributes for the node.
 * 
 * A block contains declarations. It is thus a Scope even if a separate SymbolTable is used in
 * the attributed semantics in order to manage declarations.
 * 
 * @author Marc Pantel
 *
 */
public class Block {

	/**
	 * Sequence of instructions contained in a block.
	 */
	protected List<Instruction> instructions;
    protected SymbolTable tds;
	private int memory;
	private boolean isfunctionBlock = false;
	private ClasseDeclaration classDeclaration;
	/**
	 * Constructor for a block.
	 */
	public Block(List<Instruction> _instructions) {
		this.instructions = _instructions;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _local = "";
		for (Instruction _instruction : this.instructions) {
			_local += _instruction;
		}
		return "{\n" + _local + "}\n" ;
	}
	
	/**
	 * Inherited Semantics attribute to collect all the identifiers declaration and check
	 * that the declaration are allowed.
	 * @param _scope Inherited Scope attribute that contains the identifiers defined previously
	 * in the context.
	 * @return Synthesized Semantics attribute that indicates if the identifier declaration are
	 * allowed.
	 */
	public boolean collect(HierarchicalScope<Declaration> _scope) {
		/*
		boolean _result = true;
		SymbolTable _table = new SymbolTable(_scope);
		for(Instruction _instruction: this.instructions) {
			_result = _result && _instruction.collect(_table);
		}
		return _result;*/
		
		
		this.tds = new SymbolTable(_scope);
        for (Instruction _instruction : this.instructions) {
        	if (_instruction != null) {
	            if (!_instruction.collect(tds)) {
	            	Logger.error("here" + _instruction);
	                return false;
	            }
	            
	            if (_instruction instanceof ConstantDeclaration) {
	                if (this.tds.accepts((ConstantDeclaration) _instruction)) {
	                    tds.register((ConstantDeclaration) _instruction);
	                    //System.out.println(((ConstantDeclaration)_instruction).getName());
	                } else {
	                    Logger.error("The identifier " + ((ConstantDeclaration)_instruction).getName() + " is already used.");
	                    return false;
	                }
	            }
	            if (_instruction instanceof FunctionDeclaration) {
	                if (this.tds.accepts((FunctionDeclaration) _instruction)) {
	                    tds.register((FunctionDeclaration) _instruction);
	                } else {
	                    Logger.error("The identifier " + ((FunctionDeclaration)_instruction).getName() + " is already used.");
	                    return false;
	                }
	            }
	            if (_instruction instanceof VariableDeclaration) {
	            	//System.out.println(((VariableDeclaration)_instruction).getName());
	                if (this.tds.accepts((VariableDeclaration) _instruction)) {
	                    tds.register((VariableDeclaration) _instruction);
	                } else {
	                    Logger.error("The identifier " + ((VariableDeclaration)_instruction).getName() + " is already used.");
	                    return false;
	                }
	            }
	            if (_instruction instanceof TypeDeclaration) {
	                if (this.tds.accepts((TypeDeclaration) _instruction)) {
	                    tds.register((TypeDeclaration) _instruction);
	                } else {
	                    Logger.error("The identifier " + ((TypeDeclaration)_instruction).getName() + " is already used.");
	                    return false;
	                }
	            }
            }
		}
        return true;
	}

	/**
	 * Inherited Semantics attribute to check that all identifiers have been defined and
	 * associate all identifiers uses with their definitions.
	 * @param _scope Inherited Scope attribute that contains the defined identifiers.
	 * @return Synthesized Semantics attribute that indicates if the identifier used in the
	 * block have been previously defined.
	 */
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		/*
		boolean _result = true;
		SymbolTable _table = new SymbolTable(_scope);
		for(Instruction _instruction: this.instructions) {
			_result = _result && _instruction.resolve(_table);
		}
		return _result && this.checkType();*/
		
		for (Instruction _instruction : this.instructions) {
			 if (_instruction instanceof TypeDeclaration) {
				 Type instructionType =((TypeDeclaration)_instruction).getType();
				 if( instructionType instanceof  EnumerationType) {
					 //register all the label declarations
                    for (LabelDeclaration l : ((EnumerationType)instructionType).getLabels()) {
                    	if (this.tds.accepts(l)) {
                    		l.setType((EnumerationType)instructionType);
                        	tds.register(l);
                    	} else {
                         	Logger.error("The identifier " + l.getName() + " is already used.");
                            return false;
                        }
                    }
				}
			}
			 
			if (_instruction != null) {
            if (!_instruction.resolve(tds)) {
                Logger.error("Couldn't resolve " + _instruction + " at Block.");
                return false;
            }}
		}
        return true;
	}

	/**
	 * Synthesized Semantics attribute to check that an instruction if well typed.
	 * @return Synthesized True if the instruction is well typed, False if not.
	 */	
	public boolean checkType() {
		for (Instruction _instruction : this.instructions) {
			System.out.println("Block : checktype, " + _instruction.toString());
			//System.out.println(" hello " + _instruction.checkType());
            if (!_instruction.checkType()) {
            	System.out.println(" hello ");
                Logger.error("Type Error in '' " + _instruction + " ''.");
                return false;
            }
            System.out.println(" hello ");
           
		}
        return true;
	}

	/**
	 * Inherited Semantics attribute to allocate memory for the variables declared in the instruction.
	 * Synthesized Semantics attribute that compute the size of the allocated memory. 
	 * @param _register Inherited Register associated to the address of the variables.
	 * @param _offset Inherited Current offset for the address of the variables.
	 */	
	public void allocateMemory(Register _register, int _offset) {
		int address = _offset ;
		for(Instruction ins : instructions){
			address += ins.allocateMemory(_register,address);
		}
		this.memory = address - _offset;
	}
	
	public void setAsFunctionBlock() {
		this.isfunctionBlock = true;
	}

	/**
	 * Inherited Semantics attribute to build the nodes of the abstract syntax tree for the generated TAM code.
	 * Synthesized Semantics attribute that provide the generated TAM code.
	 * @param _factory Inherited Factory to build AST nodes for TAM code.
	 * @return Synthesized AST for the generated TAM code.
	 */
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		ArrayList<FunctionDeclaration> functions = new ArrayList<FunctionDeclaration> ();
		for(Instruction ins : instructions){
			//the code generated for function declarations will be at the end after HALT
			if(ins instanceof FunctionDeclaration){
				functions.add((FunctionDeclaration) ins);
				continue;
			} else {
				frag.append(ins.getCode(_factory));
			}
		}
		if (! this.isfunctionBlock)
			frag.add(_factory.createPop(0, this.memory));
		if(!functions.isEmpty()){
			frag.add(_factory.createHalt());
			for (FunctionDeclaration f: functions) {
				frag.append(f.getCode(_factory));
			}
		}
		return frag;
	}

	public List<Instruction> getInstructions() {
		return this.instructions;
	}

	public void setClass(ClasseDeclaration c) {
    	this.classDeclaration = c;
    }
		
	

}
