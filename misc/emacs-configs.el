;; defaults that I think are good for this project
;; and some emacs comands I've written that may be helpful
;;=============================================================================

;;Insert spaces only when indenting.
;;This matters because different editors indent spaces differently
;;making code indented in one editor messed up in another.
;;then commits are dominated by whitespace changes and that sucks.
(setq-default indent-tabs-mode nil)

;; with the indent-tabs-mode disabled I still had tabs inserted for some reason
;; I use this to fix that and other whitespace issues.
(defun fix-whitespace ()
  (interactive)
  (save-excursion
    (whitespace-cleanup-region (point-min) (point-max))
    ;; remove tabs
    (untabify (point-min) (point-max))
    ;; remove excess newlines
    (goto-char 1)
    (while (re-search-forward "\n\n\n" nil t)
      (replace-match "\n\n")
      (goto-char 1))
    ;; fix indent
    (indent-region (point-min) (point-max))))





  
