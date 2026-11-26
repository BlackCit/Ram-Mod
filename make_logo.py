from PIL import Image, ImageDraw

W, H = 900, 210
img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
d = ImageDraw.Draw(img)

GREEN = (0, 238, 68, 255)
DIM_GREEN = (0, 140, 45, 255)
GRID_LINE = (0, 72, 26, 175)
BG = (5, 18, 10, 240)
WHITE = (244, 255, 247, 255)
SHADOW = (0, 125, 42, 255)

d.polygon(((52, 16), (6, 72), (52, 128), (76, 128), (30, 72), (76, 16)), fill=GREEN)
d.polygon(((848, 16), (894, 72), (848, 128), (824, 128), (870, 72), (824, 16)), fill=GREEN)

x1, y1, x2, y2 = 96, 16, 804, 128
d.rectangle((x1, y1, x2, y2), fill=BG, outline=GREEN, width=3)
for gx in range(x1 + 12, x2, 12):
    d.line(((gx, y1 + 3), (gx, y2 - 3)), fill=GRID_LINE, width=1)
for gy in range(y1 + 12, y2, 12):
    d.line(((x1 + 3, gy), (x2 - 3, gy)), fill=GRID_LINE, width=1)
F = {
    'M': ["1000001","1100011","1010101","1001001","1000001","1000001","1000001"],
    'I': ["11111","00100","00100","00100","00100","00100","11111"],
    'N': ["100001","110001","101001","100101","100011","100001","100001"],
    'D': ["111110","100001","100001","100001","100001","100001","111110"],
    'U': ["100001","100001","100001","100001","100001","100001","011110"],
    'S': ["011111","100000","100000","011110","000001","00001","111110"],
    'T': ["1111111","0001000","0001000","0001000","0001000","0001000","0001000"],
    'R': ["111110","100001","100001","111110","100100","100010","100001"],
    'Y': ["1000001","0100010","0010100","0001000","0001000","0001000","0001000"],
    'H': ["10001","10001","10001","11111","10001","10001","10001"],
    'A': ["01110","10001","10001","11111","10001","10001","10001"],
    'C': ["01111","10000","10000","10000","10000","10000","01111"],
    'K': ["10001","10010","10100","11000","10100","10010","10001"],
    'E': ["11111","10000","10000","11110","10000","10000","11111"],
    'O': ["01110","10001","10001","10001","10001","10001","01110"],
    ' ': ["000","000","000","000","000","000","000"]
}

def draw_centered(txt, cy, pw, ph, gap, col, shd=None):
    total_w = sum(len(F[c]) * pw for c in txt) + gap * (len(txt) - 1)
    x = (W - total_w) // 2
    y = cy - (7 * ph) // 2
    for ch in txt:
        g = F[ch]
        for r, row in enumerate(g):
            for c, v in enumerate(row):
                if v == '1':
                    px, py = x + c * pw, y + r * ph
                    if shd:
                        d.rectangle((px + 4, py + 4, px + pw + 3, py + ph + 3), fill=shd)
                    d.rectangle((px, py, px + pw - 1, py + ph - 1), fill=col)
        x += len(g) * pw + gap

draw_centered("MINDUSTRY", 72, 9, 10, 14, WHITE, SHADOW)

bx1, by1, bx2, by2 = 240, 142, 660, 184
d.rectangle((bx1, by1, bx2, by2), fill=BG)
for gx in range(bx1 + 8, bx2, 8):
    d.line(((gx, by1 + 2), (gx, by2 - 2)), fill=GRID_LINE, width=1)
d.line(((bx1, by1), (bx2, by1)), fill=DIM_GREEN, width=2)
d.line(((bx1, by2), (bx2, by2)), fill=DIM_GREEN, width=2)
draw_centered("HACKER EDITION", 163, 4, 4, 6, GREEN)

img.save("assets/sprites/logo.png")
print("CLEAN LOGO OK")
