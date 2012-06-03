
; Memory
(defvar *memory* (make-array (list (expt 2 16))))

(defstruct unit
  type
  psd-start
  arr-start
  val)

(defgeneric size (unit)
  (:method ((unit unit))
    (size (unit-type unit)))
  (:method ((type symbol))
	(case type
	  (:integer 1)
	  (:undefined 1)
	  (:float 2)
	  (:adress 2))))

(defun make-int (val)    (make-unit :val val :type :integer))
(defun make-float (val)  (make-unit :val val :type :float))
(defun make-adress (val) (make-unit :val val :type :adress))

(defun put-unit (adress unit)
  (setf (aref *memory* adress) unit)
  (dotimes (i (1- (size unit)) unit)
	(setf (aref *memory* (+ adress i 1))
		  (make-unit :val `(:for ,(unit-val unit)) :type :reserved))))

(defun get-unit (adress &key expected)
  (let ((unit (aref *memory* adress)))
	(if unit unit (make-unit :type (if expected expected :undefined) :val 0))))

(defun put-array (adress type length)
  (put-unit adress (make-unit :val length :type type :arr-start t)))

; Stack
(defvar *stack-top* 0)

(defun st-push (unit)
  (put-unit *stack-top* unit)
  (incf *stack-top* (size unit)))

(defun st-pop (expected)
  (format t "~a popping ~a sized: ~a ~%" *stack-top* expected (size expected))
  (decf *stack-top* (size expected))
  (format t " resulting ~a ~%" (get-unit *stack-top* :expected expected))
  (format t "111 resulting ~a ~%" (get-unit *stack-top* :expected expected))
  (get-unit *stack-top* :expected expected))

(defun reserve (size)
  (incf *stack-top* size))

(defun start-precodure ()
  (st-push (make-unit :type :undefined :psd-start t :val :precedure-start)))

(defun print-stack ()
  (format t "~%============stack(~a) after:===============" *stack-top*)
  (loop for i from 0 upto *stack-top*
		do (print (get-unit i)))
  (format t "~%"))

; Arithmetics

(defmacro ensure-type (a b type name &body body)
  `(if (and (eq (unit-type ,a) ,type)
		   	(eq (unit-type ,b) ,type))
	(progn ,@body)
	(error "~s types ~a ~a mismatch ~a" ,name (unit-type ,a) (unit-type ,b) ,type)))

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

;(defvar *cmds* (make-hash-table :test 'equal))

(defmacro defcmd (name args &body body)
  `(defun ,name ,args
	 (format t "fun: ~a, args: ~a ~%" ,(symbol-name name) (list ,@args))
	 ,@body
	 (print-stack)))

; command definitions

(defcmd ldci (const) (st-push (make-int const)))
(defcmd ldcd (const) (st-push (make-float const)))
(defcmd ldi (shift) (st-push (get-unit shift)));check for integer
(defcmd ldd (shift) (st-push (get-unit shift)));check for float
(defcmd ldsi () (st-push (get-unit (unit-val (st-pop :adress)) :expected :integer)));check for adress and integer
(defcmd ldsd () (st-push (get-unit (unit-val (st-pop :adress)) :expected :float)));check for adress and float
(defcmd sti () (put-unit (unit-val (st-pop :adress)) (st-pop :integer)));check for adress and integer
(defcmd std () (put-unit (unit-val (st-pop :adress)) (st-pop :float)));check for adress and float
(defcmd alloc (size) (reserve size))
;(defcmd scr () (st-pop))
(defcmd mri () (put-array (unit-val (st-pop :adress)) :integer (unit-val (st-pop :integer))))
(defcmd mrd () (put-array (unit-val (st-pop :adress)) :float (unit-val (st-pop :integer))))
(defcmd lda (shift) (st-push (make-adress shift)))
;(defcmd index () )
(defcmd addi () (st-push (add (st-pop :integer) (st-pop :integer) :integer))) ;check for integer
(defcmd addd () (st-push (add (st-pop :float)	(st-pop :float) :float))) ;check for float
(defcmd subi () (st-push (sub (st-pop :integer) (st-pop :integer) :integer))) ;check for integer
(defcmd subd () (st-push (sub (st-pop :float)	(st-pop :float) :float))) ;check for float
(defcmd muli () (st-push (mul (st-pop :integer) (st-pop :integer) :integer))) ;check for integer
(defcmd muld () (st-push (mul (st-pop :float)	(st-pop :float) :float))) ;check for float
(defcmd divi () (st-push (div (st-pop :integer) (st-pop :integer) :integer))) ;check for integer
(defcmd divd () (st-push (div (st-pop :float)	(st-pop :float) :float))) ;check for float
(defcmd remi () (st-push (rema (st-pop :integer) (st-pop :integer) :integer))) ;check for integer
(defcmd remd () (st-push (rema (st-pop :float)	 (st-pop :float) :float))) ;check for float
(defcmd chsi () (st-push (make-int (- (unit-val (st-pop :integer)))))) ;check for integer
(defcmd chsd () (st-push (make-float (- (unit-val (st-pop :float)))))) ;check for float
(defcmd fi2d () (st-push (make-float (unit-val (st-pop :integer))))) ;check for integer
(defcmd fd2i () (st-push (make-int (floor (unit-val (st-pop :float)))))) ;check for float
(defcmd ms () (start-precodure))
;(defcmd call () )


(alloc 203)
(ldci 100)
(lda 0)
(mad)
(lda 201)
(ldsd)
(ldci 3)
(lda 0)
(index)
(std)

;(alloc 7)
;(lda 2)
;(ldsd)
;(ldci 56)
;(ldci 8)
;(divi)

;(fi2d)
;(lda 4)
;(ldsd)
;(muld)
;(addd)
;(lda 0)
;(std)
;(ms)
;(lda 6)
;(ldsi)
;(fi2d)
;(lda 2)
;(ldsd)
;(subd)
;(ldci 1)
;;(call)
