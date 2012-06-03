
(defvar *line* nil)

(defun report (msg &rest args) (apply #'error
									  (format nil "ERROR(~a):~a" *line* msg) args))
(defun my-assert (cond msg &rest args) (unless cond (apply report msg args)))

; Memory
(defvar *memory* (make-array (list (expt 2 16))))

(defstruct unit
  type
  psd-start
  arr-start
  val)

(defgeneric size (unit)
  (:method ((unit unit))
	(if (unit-arr-start unit)
	  (size :integer)
      (size (unit-type unit))))
  (:method ((type symbol))
	(case type
	  (:integer 1)
	  (:undefined 1)
	  (:float 2)
	  (:address 2))))

(defun make-int (val)    (make-unit :val val :type :integer))
(defun make-float (val)  (make-unit :val val :type :float))
(defun make-address (val) (make-unit :val val :type :address))

(defun put-unit (address unit)
  (setf (aref *memory* address) unit)
  (dotimes (i (1- (size unit)) unit)
	(setf (aref *memory* (+ address i 1))
		  (make-unit :val `(:for ,(unit-val unit)) :type :reserved))))

(defun get-unit (address &key expected)
  (let ((unit (aref *memory* address)))
	(if unit
	  (if expected 
		(if (eq (unit-type unit) expected) unit
		  (report "expected type ~a doesn't match found: ~a~%" expected unit))
		unit)
	  (make-unit :type (if expected expected :undefined) :val 0))))

(defun put-array-head (address type length)
  (put-unit address (make-unit :val length :type type :arr-start t)))

(defun index-array (address i)
  (let ((head (get-unit address)))
	(my-assert (unit-arr-start head) "expecting array in ~a but found ~a" adress head)
	(my-assert (< -1 i (unit-val head)) "index ~a out of boundaries" i)
	(make-address (+ address (size head) (* (size (unit-type arr)) i)))))

(defun get-topmost-less (address)
  (if (eq (unit-type (get-unit address)) :reserved)
	(get-topmost-less (1- address))
	(values address (get-unit address))))

; Stack
(defvar *stack-top* 0)

(defun st-push (unit)
  (my-assert (< (+ *stack-top* (size unit)) (array-total-size *memory*))
		  "out of merory, trying allocate ~a elements" (size unit))
  (put-unit *stack-top* unit)
  (incf *stack-top* (size unit)))

(defun st-pop (expected)
  (decf *stack-top* (size expected))
  (get-unit *stack-top* :expected expected))

(defun st-pop-any ()
  (multiple-value-bind (ntop val) (get-topmost-less (1- *stack-top*))
	(setf *stack-top* ntop)
	val))

(defun reserve (size)
  (my-assert (< (+ *stack-top* size) (array-total-size *memory*))
		  "out of merory, trying allocate ~a elements" size)
  (incf *stack-top* size))

(defun start-precodure ()
  (st-push (make-unit :type :undefined :psd-start t :val :precedure-start)))

; Arithmetics

(defmacro ensure-type (a b type name &body body)
  `(if (and (eq (unit-type ,a) ,type)
		   	(eq (unit-type ,b) ,type))
	(progn ,@body)
	(report "~s types ~a ~a mismatch ~a" ,name (unit-type ,a) (unit-type ,b) ,type)))

(defun add (a b type)
  (ensure-type a b type "sub"
	(make-unit :type type :val (+ (unit-val a) (unit-val b)))))

(defun sub (a b type)
  (ensure-type a b type "sub"
	(make-unit :type type :val (- (unit-val a) (unit-val b)))))

(defun mul (a b type)
  (ensure-type a b type "mul"
	(make-unit :type type :val (* (unit-val a) (unit-val b)))))

(defun div (a b type)
  (ensure-type a b type "div"
	(make-unit :type type :val (let ((val (/ (unit-val a) (unit-val b))))
							     (if (eq type :integer) (floor val) val)))))

(defun rema (a b type)
  (ensure-type a b type "rema"
	(make-unit :type type :val (rem (unit-val a) (unit-val b)))))

; Functions

(defun get-args (&optional got)
  (let ((arg (st-pop-any)))
	(my-assert (> *stack-top* -1) "It seems, like you've forgotten to do MS")
	(if (unit-psd-start arg)
	  got
	  (get-args (cons arg got)))))

(defvar *funcs* (make-hash-table :test 'equal))

(defun call-ext-fun (num args)
  (let ((fun (gethash num *funcs*)))
	(if fun
      (funcall fun args)
	  (error "ERROR: there is no function numbered ~a~%" num))))

(defun print-units (units)
  (when units
	(print (unit-val (car units)))
	(print-units (cdr units))))

(defmacro def-ext-fun (num &body exp)
  `(setf (gethash ,num *funcs*) (lambda (args) ,@exp)))

(def-ext-fun 0 (my-assert (= (length args) 1) "too many arguments(~a) for fun" (length args))
			   (st-push (make-float (sin (unit-val (first args))))))
(def-ext-fun 1 (my-assert (= (length args) 1) "too many arguments(~a) for fun" (length args))
			   (st-push (make-float (cos (unit-val (first args))))))
(def-ext-fun 2 (print-units args))

; command definitions

(defmacro defcmd (name args &body body)
  `(defun ,name ,args
	 ,@body))

(defcmd ldci (const) (st-push (make-int const)))
(defcmd ldcd (const) (st-push (make-float const)))
(defcmd ldi (shift) (st-push (get-unit shift)))
(defcmd ldd (shift) (st-push (get-unit shift)))
(defcmd ldsi () (st-push (get-unit (unit-val (st-pop :address)) :expected :integer)))
(defcmd ldsd () (st-push (get-unit (unit-val (st-pop :address)) :expected :float)))
(defcmd sti () (put-unit (unit-val (st-pop :address)) (st-pop :integer)))
(defcmd std () (put-unit (unit-val (st-pop :address)) (st-pop :float)))
(defcmd alloc (size) (reserve size))
(defcmd scr () (st-pop-any))
(defcmd mai () (put-array-head (unit-val (st-pop :address)) :integer (unit-val (st-pop :integer))))
(defcmd mad () (put-array-head (unit-val (st-pop :address)) :float (unit-val (st-pop :integer))))
(defcmd lda (shift) (st-push (make-address shift)))
(defcmd index () (st-push (index-array (unit-val (st-pop :address)) (unit-val (st-pop :integer)))))
(defcmd addi () (st-push (add (st-pop :integer) (st-pop :integer) :integer))) 
(defcmd addd () (st-push (add (st-pop :float)	(st-pop :float) :float))) 
(defcmd subi () (st-push (sub (st-pop :integer) (st-pop :integer) :integer))) 
(defcmd subd () (st-push (sub (st-pop :float)	(st-pop :float) :float))) 
(defcmd muli () (st-push (mul (st-pop :integer) (st-pop :integer) :integer))) 
(defcmd muld () (st-push (mul (st-pop :float)	(st-pop :float) :float))) 
(defcmd divi () (st-push (div (st-pop :integer) (st-pop :integer) :integer))) 
(defcmd divd () (st-push (div (st-pop :float)	(st-pop :float) :float))) 
(defcmd remi () (st-push (rema (st-pop :integer) (st-pop :integer) :integer))) 
(defcmd remd () (st-push (rema (st-pop :float)	 (st-pop :float) :float))) 
(defcmd chsi () (st-push (make-int (- (unit-val (st-pop :integer)))))) 
(defcmd chsd () (st-push (make-float (- (unit-val (st-pop :float)))))) 
(defcmd fi2d () (st-push (make-float (unit-val (st-pop :integer))))) 
(defcmd fd2i () (st-push (make-int (floor (unit-val (st-pop :float)))))) 
(defcmd ms () (start-precodure))
(defcmd call () (call-ext-fun (unit-val (st-pop :integer)) (get-args)))

; Parser
(loop for *line* = (read-line t nil nil)
      while *line* do
	  (let ((end (position #\; *line*)))
		(when end (setf *line* (subseq *line* 0 end))))
	  (eval (read (make-string-input-stream (concatenate 'string "(" *line* ")")))))
(quit)
