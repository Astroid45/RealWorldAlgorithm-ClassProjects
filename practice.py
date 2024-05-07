class ListNode(object):
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next
        
class Solution(object):
    def addTwoNumbers( self, l1, l2): #! <--- We want to pratice this problem and understand why.
        head = ListNode()
        carry = 0
        current = head
        while l1 != None or l2 != None or carry != 0:
            l1_val = l1.val if l1 else 0 #* <-- Both statements checks if theres a value in linked list, then creates that value
            l2_val = l2.val if l2 else 0
            total = l1_val + l2_val + carry
            current.next = ListNode(total % 10) #* <-- The modulus ensures that there is no decimal for rounding down 
            carry = total//10 #* <-- Uses floor divison to take the last digit when addeding 
            l1 = l1.next if l1 else None #* <-- This iterates through the linked list
            l2 = l2.next if l2 else None
            current = current.next #* <-- Moves through created linked list that stores the values
        return head.next

def longestsubstring(s):
        seen = {}
        length = 0
        l = 0
        for r in range(len(s)):
            char = s[r]
            if char in seen and seen[char] >= l:
                l = seen[char] + 1
            else:
                length = max(length, r - l + 1)
            seen[char] = r
        return length

def findmedian(l1, l2):
    new = []
    l = 0
    for x in l1:
        new.append(x)
    for k in l2:
        new.append(k)
      
    new.sort() 
    r = len(new) - 1
    if len(new) % 2 != 0:
        x = ((len(new) - 1)//2)
        return float(new[x])
    else:
        while r != (l+1):
            r -= 1
            l += 1
        median = float((new[l] + new[r])/2.0)
        return median

from functools import reduce

def factor(n, k):
    new = list(reduce(list.__add__, ([i, n//i] for i in range(1, int(n**0.5) + 1) if n % i == 0)))
    new.sort()
    strip = []
    for x in new:
        if x not in strip:
            strip.append(x)
    strip.sort()
    if k > len(strip):
        return (-1)
    else:
        k = k-1
        for i in range(len(strip)):
            if i == k:
                return (strip[i], strip)

print(factor(n=100, k=7))
        
                
        
            
            
        
    