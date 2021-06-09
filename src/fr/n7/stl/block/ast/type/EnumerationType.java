/**
 * 
 */
package fr.n7.stl.block.ast.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.declaration.LabelDeclaration;

/**
 * @author Marc Pantel
 *
 */
public class EnumerationType implements Type, Declaration {

	private String name;

	private List<LabelDeclaration> labels;

	/**
	 * 
	 */
	public EnumerationType(String _name, List<LabelDeclaration> _labels) {
		this.name = _name;
		this.labels = _labels;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = "enum " + this.name + " { ";
		Iterator<LabelDeclaration> _iter = this.labels.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " }";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#equalsTo(fr.n7.stl.block.ast.type.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		boolean result = true;
		if(name.equals(((EnumerationType) _other).name)){
			if (((EnumerationType) _other).labels.size() == labels.size()) {
				for (int i = 0; i < labels.size(); i++) {
					result = result && labels.get(i).equals(((EnumerationType) _other).labels.get(i));
				}
			}
			else
				result = false;
			return result;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#compatibleWith(fr.n7.stl.block.ast.type.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if (_other instanceof NamedType) {
			if (((NamedType)_other).getType() instanceof EnumerationType) {
				EnumerationType temp = (EnumerationType)((NamedType)_other).getType() ;
				for(int i = 0;i<this.length(); i++) {
					String left = this.labels.get(i).getName();
					String right = temp.getLabels().get(i).getName();
					if (! left.equals(right)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#merge(fr.n7.stl.block.ast.type.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if(_other instanceof EnumerationType){
			List<LabelDeclaration> _labelsResult = new ArrayList<>();
			_labelsResult.addAll(((EnumerationType) _other).labels);
			_labelsResult.addAll(labels);
			return new EnumerationType(name,_labelsResult);
		}
		return AtomicType.ErrorType;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#length()
	 */
	@Override
	public int length() {
		return this.labels.size();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		int i=1;
		for (LabelDeclaration l : this.labels) {
			l.setType(this);
			l.setIntValue(i);
			i++;
		}
		return true;
	}
	
	public boolean contains(String _name) {
		boolean _result = false;
		Iterator<LabelDeclaration> _iter = this.labels.iterator();
		while (_iter.hasNext() && (! _result)) {
			_result = _result || _iter.next().getName().contentEquals(_name);
		}
		return _result;
	}
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this;
	}

	public List<LabelDeclaration> getLabels() {
		return this.labels;
	}
}
