$A = 1
$AUTHORS = "Oleg, Roman"

module M
  class C
    def f
    end
  end

  module M
    def f
    end

    class C
    end
  end
end

class M1
end

@a
attr_internal :a, "b"