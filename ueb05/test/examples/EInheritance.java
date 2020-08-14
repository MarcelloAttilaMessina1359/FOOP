package examples;

public class EInheritance {
    protected int top;
    
    public static class EParent extends EInheritance {
        public int parent;
    }
    
    public static class EChild extends EParent {
        public int child;
        
        public EChild init() {
            this.top = 1;
            this.parent = 2;
            this.child = 3;
            
            return this;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + child;
            result = prime * result + parent;
            result = prime * result + top;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EChild other = (EChild) obj;
            if (child != other.child)
                return false;
            if (parent != other.parent)
                return false;
            if (top != other.top)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "EChild [child=" + child + ", parent=" + parent + ", top=" + top + "]";
        }
    }
}
