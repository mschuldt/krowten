# sage
def find():
    #63 is max binary digits
    n = int(int("1"*31,2)/2)
    p = n - 10000
    prev = None
    while true:
        p = next_prime(p)
        if p > n:
            break
        prev = p
    return prev


    ## => 1073741789
