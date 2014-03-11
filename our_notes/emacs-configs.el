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
